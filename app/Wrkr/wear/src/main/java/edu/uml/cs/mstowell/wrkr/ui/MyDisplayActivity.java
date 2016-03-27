package edu.uml.cs.mstowell.wrkr.ui;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

import edu.uml.cs.mstowell.wrkr.R;
import edu.uml.cs.mstowell.wrkrlib.common.APIClientCommon;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Notification UI
 */
public class MyDisplayActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, Globals {

    private TextView mTextView;
    private APIClientCommon mApiClient;
    public static final String TAG = "wrkr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        mTextView = (TextView) findViewById(R.id.text1);

        // received a message since we're here, so init a GoogleAPIClient and respond
        mApiClient = new APIClientCommon(this);
        mApiClient.sendMessage(MSG_WEAR_MSG_ACK, "notification received");

        // TODO - remove
        SensorManager smm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensor = smm.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : sensor) {
            Log.w("wrkr", "ABCDE supplies sensor: " + s.getName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if ( mApiClient != null ) {
            //Wearable.MessageApi.removeListener( mApiClient, this );
            if ( mApiClient.isConnected() ) {
                mApiClient.disconnect();
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if( mApiClient != null )
            mApiClient.unregisterConnectionCallbacks( this );
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Wearable.MessageApi.addListener(mApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "ABCDE WEAR DISCONNECTED FROM MOBILE");
    }
}
