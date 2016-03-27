package edu.uml.cs.mstowell.wrkr.ui;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import edu.uml.cs.mstowell.wrkr.R;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Notification UI
 */
public class MyDisplayActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, Globals {

    private TextView mTextView;
    private GoogleApiClient mApiClient;
    public static final String TAG = "wrkr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        mTextView = (TextView) findViewById(R.id.text1);

        // received a message since we're here, so init a GoogleAPIClient and respond
        initGoogleApiClient();
        sendMessage(MSG_WEAR_MSG_ACK, "notification received");

        // TODO - remove
        SensorManager smm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensor = smm.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : sensor) {
            Log.w("wrkr", "ABCDE supplies sensor: " + s.getName());
        }
    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .build();

        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
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

//    @Override
//    public void onMessageReceived(MessageEvent messageEvent) {
//        Log.d(TAG, "ABCDE MESSAGE RECEIVED!!! WOO!!!!!!");
//    }

    // TODO - make a Common.java that mobile and wear share that contains common code like this method
    public void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                Log.d("wrkr", "ABCDE there are " + nodes.getNodes().size() + " nodes found");
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();

                    Log.d("wrkr", "ABCDE RESULT SEND TO MOBILE = " + result.getStatus().toString());
                }
            }
        }).start();
    }
}
