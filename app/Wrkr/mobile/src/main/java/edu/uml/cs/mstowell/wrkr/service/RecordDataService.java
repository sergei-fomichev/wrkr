package edu.uml.cs.mstowell.wrkr.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import edu.uml.cs.mstowell.wrkr.MainActivity;
import edu.uml.cs.mstowell.wrkr.R;
import edu.uml.cs.mstowell.wrkr.RestAPI;
import edu.uml.cs.mstowell.wrkrlib.common.APIClientCommon;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;
import edu.uml.cs.mstowell.wrkrlib.common.User;

/**
 * Created to show a persistent notification and ensure the service is always
 * ready to communicate wrist exercises to the API, even when the user has
 * the application closed.
 */
public class RecordDataService extends Service implements Globals {

    private final int NOTIFICATION_ID = 1;
    private boolean serviceIsRunning = false;
    private WatchToPhoneBroadcastReceiver mReceiver;
    private static Messenger messenger;
    private static APIClientCommon mApiClient;

    // default constructor
    public RecordDataService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mApiClient = new APIClientCommon(getApplicationContext());

        // declare the broadcast receiver to receive user activity updates
        mReceiver = new WatchToPhoneBroadcastReceiver();
        Log.d("wrkr", " ** service created ** ");

        //if (!serviceIsRunning) {

            // create a notification to link back to the SettingsFragment as well
            // as allow the application to record data with the screen off
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setContentIntent(pendingIntent)
                    .setContentTitle("wrkr")
                    .setContentText("Recording data from your smartwatch")
                    .setTicker("Started recording smartwatch data")
                    .setSmallIcon(R.mipmap.ic_notifcation_2)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true);

            Notification notification = builder.build();
            NotificationManager mNotificationManger =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManger.notify(NOTIFICATION_ID, notification);
            startForeground(NOTIFICATION_ID, notification);
            Log.d("wrkr", "** notification should be created **");
        //}
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //if (!serviceIsRunning) {

            // initialize variables
            serviceIsRunning = true;

            Bundle extras = intent.getExtras();
            if (extras != null)
                messenger = (Messenger) extras.get(RECORD_SERVICE_MESSENGER);

            // register the watch2phone receiver
            IntentFilter intentFilter = new IntentFilter(WATCH_TO_PHONE_BROADCAST_ACTION);
            registerReceiver(mReceiver, intentFilter);
        //}

        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        //if (serviceIsRunning) {

            // unregister the watch2phone receiver
            unregisterReceiver(mReceiver);

            // cancel the notification
            NotificationManager mNotificationManger =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManger.cancel(NOTIFICATION_ID);

            // kill the API client
            if ( mApiClient != null ) {
                if ( mApiClient.isConnected() ) {
                    mApiClient.disconnect();
                }
            }
        //}

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    public static class WatchToPhoneBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("wrkr", "ABCDE WatchToPhoneBroadcastReceiver received data from WearListener");

            String event = intent.getStringExtra(WEAR_DATA_KEY);
            byte[] rawData = intent.getByteArrayExtra(WEAR_DATA_VALUES);
            String data = new String(rawData);

            sendMessageToFragment(event, data);

            if (event == null)
                event = "ERROR";
            if (event.equals(MSG_USER_NEEDS_EXERCISE)) {
                sendUserNeedsExercise(context, Long.parseLong(data));
            }
        }
    }

    private static void sendUserNeedsExercise(Context c, long timestamp) {

        PostExerciseTask pet = new PostExerciseTask();
        pet.c = c;
        pet.timestamp = timestamp;
        pet.execute();
    }

    private static class PostExerciseTask extends AsyncTask<Void, Void, Void> {

        public Context c;
        public long timestamp;

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences prefs = c.getSharedPreferences(GLOBAL_PREFS, 0);
            SharedPreferences.Editor edit = prefs.edit();

            int uid = prefs.getInt(USER_ID, -1);
            String strEmail = prefs.getString(USER_EMAIL, "");

            if (uid == -1) {
                // need to obtain the uid from the API
                User u = RestAPI.getUser(strEmail);
                if (u == null) {
                    // TODO - handle case where user isn't set up in the DB yet - probably just
                    // TODO   prevent this whole thing from happening and be like YOU MUST SET THIS UP NOW
                    Log.e("wrkr", "USER IS NULL!");
                } else {
                    uid = u.id;
                    edit.putInt(USER_ID, uid).apply();
                }
            }

            // send the server that the user has an exercise due
            User u = RestAPI.postExercise(uid, timestamp);
            if (u == null) {
                // TODO - handle this case
                Log.e("wrkr", "USER IS NULL AFTER POST EXERCISE!");
            } else {
                Log.d("wrkr", "ABCDE user " + uid + " has " + u.exercises + " exercise(s) due");
            }

            // send the watch a notification (acts as an ACK)
            mApiClient.sendMessage(MSG_WRIST_EXER_TIME, "");
            return null;
        }
    }

    private static void sendMessageToFragment(String type, String optionMsgStr) {

        Message msg = Message.obtain();

        Bundle data = new Bundle();
        data.putString(WEAR_DATA_KEY, type);
        data.putString(WEAR_DATA_VALUES, optionMsgStr);

        msg.setData(data);

        try {
            if (messenger != null)
                messenger.send(msg);
        } catch (Exception e) {/* ignore error if any occur */}
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
