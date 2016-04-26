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
import android.util.Log;

import edu.uml.cs.mstowell.wrkr.R;
import edu.uml.cs.mstowell.wrkr.object.RestAPI;
import edu.uml.cs.mstowell.wrkr.ui.MainActivity;
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
    private static APIClientCommon mApiClient;
    private static Context mContext;

    // default constructor
    public RecordDataService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        // register GoogleApiClient
        mApiClient = new APIClientCommon(getApplicationContext());

        // declare the broadcast receiver to receive user activity updates
        mReceiver = new WatchToPhoneBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (serviceIsRunning)
            return START_STICKY; // do not re-run below code is service is already started

        // initialize running state
        serviceIsRunning = true;

        // register the watch2phone receiver
        IntentFilter intentFilter = new IntentFilter(WATCH_TO_PHONE_BROADCAST_ACTION);
        registerReceiver(mReceiver, intentFilter);

        // create a notification to link back to the application as well
        // as keep the application alive to receive comm from the wear side
        Intent notifIntent = new Intent(getApplicationContext(), MainActivity.class);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                NOTIFICATION_ID, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
        startForeground(NOTIFICATION_ID, notification); // start as a foreground process

        return START_STICKY; // reschedule the service if killed by the system
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        // unregister the watch2phone receiver
        unregisterReceiver(mReceiver);

        // cancel the notification
        NotificationManager mNotificationManger =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManger.cancel(NOTIFICATION_ID);

        // kill the API client
        if (mApiClient != null) {
            if (mApiClient.isConnected()) {
                mApiClient.disconnect();
            }
        }
    }

    public static class WatchToPhoneBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // received data from wearable
            String event = intent.getStringExtra(WEAR_DATA_KEY);
            if (event == null)
                event = "ERROR";
            byte[] rawData = intent.getByteArrayExtra(WEAR_DATA_VALUES);
            String data = new String(rawData);

            // send data to SettingsFragment for view
            sendMessageToFragment(event, data);

            if (event.equals(MSG_USER_NEEDS_EXERCISE)) {
                // if the user needs an exercise, communicate so with the API
                sendUserNeedsExercise(context, Long.parseLong(data));
            }
        }
    }

    private static void sendUserNeedsExercise(Context c, long timestamp) {

        // execute the API call in the background
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
                    Log.e("wrkr", "null user");
                } else {
                    uid = u.id;
                    edit.putInt(USER_ID, uid).apply();
                }
            }

            // send the server that the user has an exercise due
            User u = RestAPI.postExercise(uid, timestamp);
            if (u == null) {
                Log.e("wrkr", "null user");
            }

            // send the watch a notification (acts as an ACK) and vibrate the user's wrist
            mApiClient.sendMessage(MSG_WRIST_EXER_TIME, "");
            return null;
        }
    }

    private static void sendMessageToFragment(String key, String value) {

        Bundle data = new Bundle();
        data.putString(WEAR_DATA_KEY, key);
        data.putString(WEAR_DATA_VALUES, value);

        // send broadcast to SettingsFragment to display data from the wearable
        Intent intent = new Intent();
        intent.setAction(SETTINGS_RCV_ACTION);
        intent.putExtra(SETTINGS_FRAG_BUNDLE, data);
        mContext.sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
