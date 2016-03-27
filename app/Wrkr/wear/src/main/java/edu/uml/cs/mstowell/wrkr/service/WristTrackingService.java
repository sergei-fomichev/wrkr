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

import java.util.Timer;
import java.util.TimerTask;

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

    private WristTrackingListener mWristListener;
    private WristBroadcastReceiver mReceiver;
    private APIClientCommon mApiClient;

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

        // initialize GoogleAPIClient
        mApiClient = new APIClientCommon(this);

        // create a notification to link back to the RunFragment as well
        // as allow the application to record data with the screen off
        if (!serviceIsRunning) {

            // send a message to the app to display a notification that
            // recording has started
            mApiClient.sendMessage(MSG_START_ACCEL_ACK, "Starting wear accelerometer now");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!serviceIsRunning) {

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
            resetAccelTimer = new Timer();
            resetAccelTimer.scheduleAtFixedRate(task, 0, 300000); // 5*60*1000 = 5 minutes
        }

        return super.onStartCommand(intent, flags, startId);
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
            Log.d("wrkr", "ABCDE reseting the accel");
            setAccelListener();

            timerTicks++;
        }
    }

    private void setAccelListener() {
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.unregisterListener(mWristListener);
        mSensorManager.registerListener(mWristListener, accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);//, 10000000);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (serviceIsRunning) {

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
        }

        // send the app a termination message
        mApiClient.sendMessage(MSG_STOP_ACCEL_ACK, "stopping wear accelerometer");
    }

    private class WristBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // get the broadcasted 10 seconds worth of data
            String data = intent.getStringExtra(WRIST_BROADCAST_DATA);

            // send the data back to the wrkr mobile app
            mApiClient.sendMessage(MSG_WEAR_DATA, data);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
