package edu.uml.cs.mstowell.wrkr.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Collects wear accelerometer data and does some feature calculations for machine learning
 * on the wrkr mobile side (some code from Mike's RunningStart app)
 */
public class WristTrackingListener implements SensorEventListener, Globals {

    private long mLastAccelUpdate;
    private float[] mPreviousAccelReading;
    private LinkedList<Double> mWMAList;
    private JSONObject data;
    private JSONArray jaX, jaY, jaZ, jaMag, jaWMA;
    private int dataIndex;

    private Context mContext;

    // resusable variables
    long thisAccelUpdate;
    float x, y, z, xP, yP, zP;
    double magnitude, magnitudeP, alpha, d, wma;

    public WristTrackingListener(Context c) {

        mContext = c;
        resetData();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            Log.i("wrkr", "event");

            // get event's timestamp
            thisAccelUpdate = event.timestamp;

            // only consider user activity updates roughly every 1/5 second (1000^3 = 1 second)
            // 200000000 = 1/5 second but 175000000 allows some flexibility
            if (mLastAccelUpdate != 0 && thisAccelUpdate - mLastAccelUpdate < 175000000) {
                return;
            }

            // update the timestamp
            mLastAccelUpdate = thisAccelUpdate;

            // calculate user's activity level

            // retrieve accelerometer event values
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            // if we don't have a previous accelerometer reading, we will store one and return
            if (mPreviousAccelReading == null) {

                mPreviousAccelReading = new float[3];
                mPreviousAccelReading[0] = x;
                mPreviousAccelReading[1] = y;
                mPreviousAccelReading[2] = z;
                return;
            }

            // get the previous accel reading values
            xP = mPreviousAccelReading[0];
            yP = mPreviousAccelReading[1];
            zP = mPreviousAccelReading[2];

            // calculate the magnitude of this and the previous accelerometer vectors
            magnitude = Math.sqrt((x*x) + (y*y) + (z*z));
            magnitudeP = Math.sqrt((xP*xP) + (yP*yP) + (zP*zP));

            // calculate alpha, the angle between the two consecutive accelerometer readings
            alpha = ((x * xP) + (y * yP) + (z * zP)) / (magnitude * magnitudeP);

            // then calculate d sub i, the inverse cos of alpha
            d = Math.acos(alpha);

            // if the WMA (weighted moving average) list is null, initialize it
            if (mWMAList == null)
                mWMAList = new LinkedList<>();

            // if the WMA list is full (capacity = 10), pop off; then,
            // add the new d sub i value to the WMA list
            if (mWMAList.size() == 10) {
                // cycle the list backwards (essentially a pop/add without expanding memory)
                mWMAList.set(9, mWMAList.get(8));
                mWMAList.set(8, mWMAList.get(7));
                mWMAList.set(7, mWMAList.get(6));
                mWMAList.set(6, mWMAList.get(5));
                mWMAList.set(5, mWMAList.get(4));
                mWMAList.set(4, mWMAList.get(3));
                mWMAList.set(3, mWMAList.get(2));
                mWMAList.set(2, mWMAList.get(1));
                mWMAList.set(1, mWMAList.get(0));
                mWMAList.set(0, d);
            } else {
                mWMAList.add(d);
            }

            // if the WMA list does not yet have 10 values in it, return
            if (mWMAList.size() != 10)
                return;

            // get a weighted moving average
            wma = ((10 * mWMAList.get(0)) + (9 * mWMAList.get(1)) +
                    (8 * mWMAList.get(2)) + (7 * mWMAList.get(3)) + (6 * mWMAList.get(4)) +
                    (5 * mWMAList.get(5)) + (4 * mWMAList.get(6)) + (3 * mWMAList.get(7)) +
                    (2 * mWMAList.get(8)) + (mWMAList.get(9))) / 55;

            // update the previous accel reading to this reading
            mPreviousAccelReading[0] = x;
            mPreviousAccelReading[1] = y;
            mPreviousAccelReading[2] = z;

            Log.d("wrkr", "ABCDE :::: x = " + x + ", y = " + y + ", z =" + z
                    + ", m = " + magnitude + ", w = " + wma);

            // broadcast the data when we have a valid wma
            if (!Double.isNaN(wma)) {

                // record the data (as strings)
                jaX.put(""+x);
                jaY.put(""+y);
                jaZ.put(""+z);
                jaMag.put(""+magnitude);
                jaWMA.put(""+wma);
                dataIndex++;

                // after we collect 50 points (~10 seconds of data), send back to device
                if (dataIndex > (DATA_SIZE - 1)) {

                    // set up data JSONObject
                    try {
                        data.put("x", jaX);
                        data.put("y", jaY);
                        data.put("z", jaZ);
                        data.put("mag", jaMag);
                        data.put("wma", jaWMA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // send broadcast
                    Intent intent = new Intent();
                    intent.setAction(WRIST_BROADCAST_ACTION);
                    intent.putExtra(WRIST_BROADCAST_DATA, data.toString());
                    mContext.sendBroadcast(intent);

                    // reset the data array and data index
                    resetData();
                }
            }
        }
    }

    private void resetData() {
        data = new JSONObject();
        jaX = new JSONArray();
        jaY = new JSONArray();
        jaZ = new JSONArray();
        jaMag = new JSONArray();
        jaWMA = new JSONArray();
        dataIndex = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not implemented
    }
}
