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
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import edu.uml.cs.mstowell.wrkr.R;
import edu.uml.cs.mstowell.wrkrlib.common.APIClientCommon;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Wear accelerometer data collection service
 */
public class WristTrackingService extends Service implements Globals {

    private boolean serviceIsRunning;
    private SensorManager mSensorManager;
    private Timer resetAccelTimer;
    private int timerTicks = 0;
    private Context mContext;

    private WristTrackingListener mWristListener;
    private WristBroadcastReceiver mReceiver;
    private APIClientCommon mApiClient;

    private PowerManager.WakeLock mWakeLock;
    private int NOTIFICATION_ID = 55;

    private Vibrator vibrator;
    private long[] pattern = {0, 300, 50, 600};
    private SharedPreferences prefs;

    // default constructor
    public WristTrackingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWristListener = new WristTrackingListener(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mContext = getApplicationContext();

        // declare the broadcast receiver to receive user activity updates
        mReceiver = new WristBroadcastReceiver();

        // initialize GoogleAPIClient
        mApiClient = new APIClientCommon(this);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        prefs = mContext.getSharedPreferences(GLOBAL_PREFS, 0);

        // create a notification to link back to the RunFragment as well
        // as allow the application to record data with the screen off
        //if (!serviceIsRunning) {

            // send a message to the app to display a notification that
            // recording has started
            mApiClient.sendMessage(MSG_START_ACCEL_ACK, "Starting wear accelerometer now");

            // create a partial wakelock to keep accelerometer readings coming
            setWakelock();

            // create a notification to keep the accelerometer on
            final Intent emptyIntent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    NOTIFICATION_ID, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setContentIntent(pendingIntent)
                    .setContentTitle("wrkr")
                    .setContentText("Currently recording data")
                    .setTicker("Keep this notification to keep the accelerometer on")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true);

            Notification notification = builder.build();
            builder.setOngoing(true); // TODO
            NotificationManager mNotificationManger =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManger.notify(NOTIFICATION_ID, notification);
            startForeground(NOTIFICATION_ID, notification);
        //}
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //super.onStartCommand(intent, flags, startId);

        //if (!serviceIsRunning) {

            // initialize variables
            serviceIsRunning = true;

            // register the wrist tracking receiver
            IntentFilter intentFilter = new IntentFilter(WRIST_BROADCAST_ACTION);
            registerReceiver(mReceiver, intentFilter);

            /*
            The Moto 360 device cannot support batched data collection mode in its hardware.
            int fifoSize = accelerometer.getFifoReservedEventCount();
            The above code reports a fifoSize of 0.  We would need a fifoSize > 0 to do this.
            */

            // register accelerometer listener
            setAccelListener();

            // start a timer to re-register the accelerometer listener
            ResetAccelTimerTask task = new ResetAccelTimerTask();
            resetAccelTimer = new Timer(); // TODO - below is 10 seconds, set back after test
            resetAccelTimer.scheduleAtFixedRate(task, 0, 10000);//300000); // 5*60*1000 = 5 minutes
        //}

        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    private class ResetAccelTimerTask extends TimerTask {
        @Override
        public void run() {

            // if the accelerometer has been running for 10 hours straight,
            // turn it off (could be due to mistake)
            if (timerTicks >= 120) { // 120 = 12 5-minute ticks per hour * 10 hours
                stopSelf();
            }

            // make sure the accelerometer never turns off by reseting it
//            Log.d("wrkr", "ABCDE reseting the accel");
//            AsyncTask.execute(new Runnable() { // TODO no idea if doing this in AsyncTask helps
//                @Override
//                public void run() {
//                    //setAccelListener();
//                    //destroyWakelock(); // TODO does this work?
//                    //setWakelock();
//                }
//            });

            timerTicks++;
        }
    }

    private void setAccelListener() {
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.unregisterListener(mWristListener);
        mSensorManager.registerListener(mWristListener, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);//, 10000000);
        // TODO - also try 200000 above for sensor delay
    }

    private void setWakelock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        mWakeLock.acquire();
    }

    private void destroyWakelock() {
        if (mWakeLock != null && mWakeLock.isHeld())
            mWakeLock.release();
    }

    @Override
    public void onDestroy() {

        // send the app a termination message
        mApiClient.sendMessage(MSG_STOP_ACCEL_ACK, "stopping wear accelerometer");

        //if (serviceIsRunning) {

            // unregister accelerometer updates
            mSensorManager.unregisterListener(mWristListener);

            // cancel the timer
            if (resetAccelTimer != null)
                resetAccelTimer.cancel();
            resetAccelTimer = null;

            // unregister the broadcast receiver
            unregisterReceiver(mReceiver);

            // disconnect the Google API Client
            if ( mApiClient != null ) {
                if ( mApiClient.isConnected() ) {
                    mApiClient.disconnect();
                }
            }
        //}

        // cancel the notification
        NotificationManager mNotificationManger =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManger.cancel(NOTIFICATION_ID);

        // always ensure the wakelock is dismissed
        destroyWakelock();

        super.onDestroy();
    }

    private final class WristBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {

            // perform in background to allow the broadcastreceiver to return quickly
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d("wrkr", "ABCDE ######### >>>>> READY <<<<<<<< ########");

                    // get the broadcasted 10 seconds worth of data
                    String data = intent.getStringExtra(WRIST_BROADCAST_DATA);

                    // TODO testing
//                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//                    long[] pattern = {0, 300, 50, 600};
                    vibrator.vibrate(pattern, -1); // TODO - this replaced MobileListenerService vibrate
                    ///////////////////////

                    // determine if the user is at the keyboard
                    boolean atKeyboard = classify(data);
                    Log.d("wrkr", "ABCDE - at keyboard? *******" + atKeyboard + "*******");
                    if (atKeyboard)
                        incrementUserKeyboardTime();

                    // send the data back to the wrkr mobile app- TODO just for debugging, can remove later
                    mApiClient.sendMessage(MSG_WEAR_DATA, data);
                    Log.d("wrkr", "ABCDE ######### >>>>>  SENT  <<<<<<<< ########");
                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // determine if the user is at a keyboard
    private JSONObject dataJO;
    private JSONArray x;
    private JSONArray z;
    private JSONArray wma;
    private boolean classify(String data) {

        try {
            dataJO = new JSONObject(data);

            x = dataJO.getJSONArray("x");
            //JSONArray y = dataJO.getJSONArray("y");
            z = dataJO.getJSONArray("z");
            //JSONArray mag = dataJO.getJSONArray("mag");
            wma = dataJO.getJSONArray("wma");

            int p = 0;

            /*
             * For now, we will use a bounded-box classifier based on our current training data.
             * This assumes that X, Z, and WMA will all fall within the bound below.
             * In the future, we should use a proper estimation maximization ML algorithm.
             */
            for (int i = 0; i < x.length(); i++) {
                if (x.getDouble(i) > -1 && x.getDouble(i) < 4 &&
                        z.getDouble(i) > 5.5 && z.getDouble(i) < 11.5 &&
                        wma.getDouble(i) > 0 && wma.getDouble(i) < 0.4) {
                    p++;
                }
            }

            // get the likelihood the user is at the keyboard
            double likelihood = ((double) p) / (double)x.length();
            Log.d("wrkr", "ABCDE - likelihood = " + likelihood);
            if (likelihood > LIKELIHOOD_PERCENTAGE) {
                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    // user is at a keyboard - increment their time count
    private void incrementUserKeyboardTime() {

        SharedPreferences.Editor edit = prefs.edit();
        int timeAtKeyboard = prefs.getInt(USER_TIME_AT_KEYBOARD, 0);
        timeAtKeyboard += (DATA_SIZE / DATA_HERTZ);

        if (timeAtKeyboard >= 20) { //>= EXERCISE_TRIGGER_TIME) { // TODO - set to 20 for testing
            Log.d("wrkr", "ABCDE Time for an exercise!");
            sendUserNeedsExercise();

            // set the time at keyboard back to 0
            edit.putInt(USER_TIME_AT_KEYBOARD, 0).apply();
        } else {
            // update the time at keyboard
            edit.putInt(USER_TIME_AT_KEYBOARD, timeAtKeyboard).apply();
        }
    }

    private void sendUserNeedsExercise() {

        final long timestamp = System.currentTimeMillis();

//        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//        long[] pattern = {0, 300, 50, 600};
//        vibrator.vibrate(pattern, -1); // TODO - this replaced MobileListenerService vibrate

        if (mApiClient.areWatchAndPhonePaired())
            mApiClient.sendMessage(MSG_USER_NEEDS_EXERCISE, "" + timestamp);
        else {
            // phone and watch are not paired currently - start a retry timer
            final Timer timer = new Timer();
            final TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (mApiClient.areWatchAndPhonePaired()) {
                        mApiClient.sendMessage(MSG_USER_NEEDS_EXERCISE, "" + timestamp);
                        timer.cancel();
                        timer.purge();
                    }
                }
            };
            timer.schedule(task, 300000); // 5 minutes
        }
    }
}
