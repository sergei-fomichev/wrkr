package edu.uml.cs.mstowell.wrkr.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.LinkedList;

import edu.uml.cs.mstowell.wrkrlib.data.Globals;

/**
 * Created by Mike on 3/14/2016.
 */
public class WristTrackingListener implements SensorEventListener, Globals {

    private long mLastAccelUpdate;
    private float[] mPreviousAccelReading;
    private LinkedList<Double> mWMAList;

    private Context mContext;

    private int userActivity;
    private int broadcastedActivity;

    // declare an agreement constant, which enforces that the user's activity
    // level will not be broadcast until the user has been consistently showing
    // a particular activity level for 5 (agreement >= 5) consecutive occurrences
    private int agreement;

    // TODO - we need some sort of wakelock (but try batching first) to keep the accelerometer
    // TODO - running for a long period of time
    public WristTrackingListener(Context c) {

        mContext = c;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // get event's timestamp
            long thisAccelUpdate = event.timestamp;

            // only consider user activity updates every 1/5 second (1000^3 = 1 second)
            if (mLastAccelUpdate != 0 && thisAccelUpdate - mLastAccelUpdate < 1000*1000*200) {
                return;
            }

            // update the timestamp
            mLastAccelUpdate = thisAccelUpdate;

            // calculate user's activity level

            // retrieve accelemeter event values
            float x = event.values[0],
                    y = event.values[1],
                    z = event.values[2];

            // if we don't have a previous accelerometer reading, we will store one
            // and return
            if (mPreviousAccelReading == null) {

                mPreviousAccelReading = new float[3];
                mPreviousAccelReading[0] = x;
                mPreviousAccelReading[1] = y;
                mPreviousAccelReading[2] = z;
                return;
            }

            // get the previous accel reading values
            float xP = mPreviousAccelReading[0],
                    yP = mPreviousAccelReading[1],
                    zP = mPreviousAccelReading[2];

            // calculate the magnitude of this and the previous accelerometer vectors
            double magnitude = Math.sqrt((x*x) + (y*y) + (z*z));
            double magnitudeP = Math.sqrt((xP*xP) + (yP*yP) + (zP*zP));

            // calculate alpha, the angle between the two consecutive accelerometer readings
            double alpha = ((x * xP) + (y * yP) + (z * zP)) / (magnitude * magnitudeP);

            // then calculate d sub i, the inverse cos of alpha
            double d = Math.acos(alpha);

            // if the WMA (weighted moving average) list is null, initialize it
            if (mWMAList == null)
                mWMAList = new LinkedList<Double>();

            // if the WMA list is full (capacity = 10), pop off
            if (mWMAList.size() == 10)
                mWMAList.remove();

            // add the new d sub i value to the WMA list
            mWMAList.add(d);

            // if the WMA list does not yet have 10 values in it, return
            if (mWMAList.size() != 10)
                return;

            // get a weighted moving average
            double wma = ((10 * mWMAList.get(0)) + (9 * mWMAList.get(1)) +
                    (8 * mWMAList.get(2)) + (7 * mWMAList.get(3)) + (6 * mWMAList.get(4)) +
                    (5 * mWMAList.get(5)) + (4 * mWMAList.get(6)) + (3 * mWMAList.get(7)) +
                    (2 * mWMAList.get(8)) + (mWMAList.get(9))) / 55;


            // update the previous accel reading to this reading
            mPreviousAccelReading[0] = x;
            mPreviousAccelReading[1] = y;
            mPreviousAccelReading[2] = z;

            // broadcast raw accel readings // TODO - here would be the spot
            Log.d("wrkr", "ABCDE :::: x = " + x + ", y = " + y + ", z =" + z + ", m = " + magnitude);

            // decide if the user's activity has changed
            int newUserActivity = getUserActivityLevel(wma);
            if (userActivity != newUserActivity) {

                // record the new user activity and reset the agreement counter
                userActivity = newUserActivity;
                agreement = 0;

            } else {

                if (agreement < 5) {

                    // the user's activity level is the same as it was last,
                    // so update the agreement count
                    agreement++;

                } else if (userActivity != broadcastedActivity) {

                    // the user's activity has not not changed and has agreed for a while
                    // now - broadcast this activity change since it is also different from
                    // last broadcasted user activity
                    Intent intent = new Intent();
                    intent.setAction(WRIST_BROADCAST_ACTION);
                    intent.putExtra(WRIST_BROADCAST_ACTIVITY_UPDATE, userActivity);

                    mContext.sendBroadcast(intent);
                    broadcastedActivity = userActivity;
                }
            }
        }
    }

    // based on the weighted moving average of the user, approximate the user's
    // current activity level
    private int getUserActivityLevel(double wma) {

        if (wma <= 0.2) {
            return USER_ACTIVITY_STANDING;
        } else if (wma > 0.2 && wma <= 0.7) {
            return USER_ACTIVITY_WALKING;
        } else if (wma > 0.7 && wma <= 2.5) {
            return USER_ACTIVITY_RUNNING;
        } else { // wma > 2.5
            return USER_ACTIVITY_CHEATING;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not implemented
    }
}
