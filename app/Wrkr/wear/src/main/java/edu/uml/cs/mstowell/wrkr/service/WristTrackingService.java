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
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.uml.cs.mstowell.wrkr.R;
import edu.uml.cs.mstowell.wrkrlib.common.APIClientCommon;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;
import edu.uml.cs.mstowell.wrkr.ml.Logistic;

/**
 * Wear accelerometer data collection service
 */
public class WristTrackingService extends Service implements Globals {

    private boolean serviceIsRunning;
    private SensorManager mSensorManager;
    private Timer resetAccelTimer;
    private int timerTicks = 0;
    private Context mContext;

    private Logistic logistic;
    private double[] weights;

    private WristTrackingListener mWristListener;
    private WristBroadcastReceiver mReceiver;
    private APIClientCommon mApiClient;

    private PowerManager.WakeLock mWakeLock;
    private int NOTIFICATION_ID = 55;

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

        // train the logistic regression model, if needed
        prefs = mContext.getSharedPreferences(GLOBAL_PREFS, 0);
        trainLogisticModel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.w("wrkr", "ON START COMMAND - " + serviceIsRunning);

        if (serviceIsRunning)
            return START_STICKY; // do not re-run below code is service is already started

        // initialize running state
        serviceIsRunning = true;

        // send a message to the app to display a notification that
        // recording has started
        mApiClient.sendMessage(MSG_START_ACCEL_ACK, "Starting wear accelerometer now");

        // create a partial wakelock to keep accelerometer readings coming
        setWakelock();

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

        // start a timer to re-register the accelerometer listener every 30 seconds
        ResetAccelTimerTask task = new ResetAccelTimerTask();
        resetAccelTimer = new Timer();
        resetAccelTimer.scheduleAtFixedRate(task, 0, 30000);

        // create a persistent notification to allow the accelerometer to always run
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
        builder.setOngoing(true);
        NotificationManager mNotificationManger =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManger.notify(NOTIFICATION_ID, notification);
        startForeground(NOTIFICATION_ID, notification);

        return START_STICKY; // reschedule the service if killed by the system
    }

    private void trainLogisticModel() {

        final SharedPreferences.Editor edit = prefs.edit();
        logistic = new Logistic(mContext);

        boolean isTrained = prefs.getBoolean(LOGISTIC_MODEL_TRAINED, false);
        if (isTrained) {
            getWeightsFromPrefs();
            logistic.setWeights(weights);
        } else {
            try {
                weights = logistic.runLogisticRegression();
                for (int i = 0; i < NUM_FEATURES; i++)
                    edit.putString(LOGISTIC_WEIGHTS + i, "" + weights[i]);
                edit.putBoolean(LOGISTIC_MODEL_TRAINED, true);
                edit.apply();
                Log.d("wrkr", "weights: " + Arrays.toString(weights));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getWeightsFromPrefs() {

        weights = new double[NUM_FEATURES];
        for (int i = 0; i < NUM_FEATURES; i++) {
            weights[i] = Double.parseDouble(prefs.getString(LOGISTIC_WEIGHTS + i, "0.0"));
        }
    }

    private class ResetAccelTimerTask extends TimerTask {
        @Override
        public void run() {

            // if the accelerometer has been running for 10 hours straight,
            // turn it off (for sanity purposes)
            if (timerTicks >= 1200) { // 1200 = 120 30-second ticks per hour * 10 hours
                stopSelf();
            }

            // make sure the accelerometer never turns off by resetting it
            Log.d("wrkr", "ABCDE reseting the accel");
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    setAccelListener();
                    //destroyWakelock();
                    //setWakelock();
                }
            });

            timerTicks++;
        }
    }

    private void setAccelListener() {
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.unregisterListener(mWristListener);
        mSensorManager.registerListener(mWristListener, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
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

        // unregister accelerometer updates
        mSensorManager.unregisterListener(mWristListener);

        // cancel the timer
        if (resetAccelTimer != null)
            resetAccelTimer.cancel();
        resetAccelTimer = null;

        // unregister the broadcast receiver
        unregisterReceiver(mReceiver);

        // disconnect the Google API Client
        if (mApiClient != null) {
            if (mApiClient.isConnected()) {
                mApiClient.disconnect();
            }
        }

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

                    // get the broadcasted 10 seconds worth of data
                    String data = intent.getStringExtra(WRIST_BROADCAST_DATA);

                    // determine if the user is at the keyboard
                    boolean atKeyboard = classify(data);
                    Log.d("wrkr", "ABCDE - at keyboard? *******" + atKeyboard + "*******");
                    if (atKeyboard)
                        incrementUserKeyboardTime();

                    // send the data back to the wrkr mobile app- TODO just for debugging, can remove later
                    mApiClient.sendMessage(MSG_WEAR_DATA, data);
                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // determine if the user is at a keyboard
    @SuppressWarnings("all") private JSONArray x;    //
    @SuppressWarnings("all") private JSONArray y;    //
    @SuppressWarnings("all") private JSONArray z;    // these are all reusable variables
    @SuppressWarnings("all") private JSONArray mag;  //
    @SuppressWarnings("all") private JSONArray wma;  //
    @SuppressWarnings("all") private double dataPoint[] = new double[NUM_FEATURES];
    private boolean classify(String data) {

        try {
            JSONObject dataJO = new JSONObject(data);

            x = dataJO.getJSONArray("x");
            y = dataJO.getJSONArray("y");
            z = dataJO.getJSONArray("z");
            mag = dataJO.getJSONArray("mag");
            wma = dataJO.getJSONArray("wma");

            List<Double> prob = new LinkedList<>();

            // classify each data point with the logistic regression model
            for (int i = 0; i < x.length(); i++) {

                dataPoint[0] = x.getDouble(i);
                dataPoint[1] = y.getDouble(i);
                dataPoint[2] = z.getDouble(i);
                dataPoint[3] = mag.getDouble(i);
                dataPoint[4] = wma.getDouble(i);

                prob.add(logistic.classify(dataPoint));
            }

            double likelihood = logistic.mean(prob);
            Log.d("wrkr", "p(1|x) = " + likelihood);
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
