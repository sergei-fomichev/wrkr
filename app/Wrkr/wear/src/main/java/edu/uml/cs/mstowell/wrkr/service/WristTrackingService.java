package edu.uml.cs.mstowell.wrkr.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import edu.uml.cs.mstowell.wrkrlib.data.Globals;

/**
 * Created by Mike on 3/14/2016.
 */
public class WristTrackingService extends Service implements Globals {

    private boolean serviceIsRunning;
    private SensorManager mSensorManager;

    private WristTrackingListener mWristListener;
    private WristBroadcastReceiver mReceiver;

    private int secondsRan;
    private boolean userIsSitting;

    // default constructor
    public WristTrackingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWristListener = new WristTrackingListener(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // declare the broadcast receiver to receive user activity updates
        mReceiver = new WristBroadcastReceiver();

        // create a notification to link back to the RunFragment as well
        // as allow the application to record data with the screen off
        if (!serviceIsRunning) {

            // TODO - send a message to the app to display a notification that
            // TODO - recording has started
            Log.w("wrkr", "ABCDE ACCELEROMETER STARTING");

//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
//                    NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            Notification.Builder builder = new Notification.Builder(getApplicationContext());
//            builder.setContentIntent(pendingIntent)
//                    .setContentTitle("Running Start")
//                    .setContentText("Recording run data")
//                    .setTicker("Started recording run data")
//                    .setSmallIcon(R.drawable.ic_launcher)
//                    .setContentIntent(pendingIntent);
//
//            Notification notification = builder.build();
//            NotificationManager mNotificationManger =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManger.notify(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!serviceIsRunning) {

            // initialize variables
            serviceIsRunning = true;
            userIsSitting = false;
            secondsRan = 0;

            // register the wrist tracking receiver
            IntentFilter intentFilter = new IntentFilter(WRIST_BROADCAST_ACTION);
            registerReceiver(mReceiver, intentFilter);

            // register to receive accelerometer updates
            mSensorManager.registerListener(mWristListener,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_FASTEST);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (serviceIsRunning) {

            // unregister accelerometer updates
            mSensorManager.unregisterListener(mWristListener);

            // unregister the broadcast receiver
            unregisterReceiver(mReceiver);
        }

        // TODO - send the app a termination message, possibly display another notification
        Log.w("wrkr", "ABCDE ACCELEROMETER TERMINATING");
    }

    private class WristBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // get the user's newly broadcast activity level
            int activity = intent.getIntExtra(WRIST_BROADCAST_ACTIVITY_UPDATE, -1);

            // TODO - send back some data
            if (activity != -1) {
                // data is a change in activity
                Log.d("wrkr", "ABCDE activity = " + activity);
            } else {
                // data is some raw accel readings
                // TODO - do something with these
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
