package edu.uml.cs.mstowell.wrkr;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

public class MyDisplayActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {

    private TextView mTextView;
    private GoogleApiClient mApiClient;
    public static final String TAG = "wrkr";
    public static final String PATH = "/wrkrnotification";

    // wear comm
    final String START_ACTIVITY = "/start_activity";
    final String WRIST_EXER_TIME = "/wrist_exer_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        mTextView = (TextView) findViewById(R.id.text1);

        initGoogleApiClient();
//        fireMessage();
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
            Wearable.MessageApi.removeListener( mApiClient, this );
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
        Wearable.MessageApi.addListener( mApiClient, this );
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "ABCDE WEAR DISCONNECTED FROM MOBILE");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Toast.makeText(getApplicationContext(), "WOO!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "ABCDE MESSAGE RECEIVED!!! WOO!!!!!!");
    }


//    private void fireMessage() {
//        // Send the RPC
//        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient);
//        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
//            @Override
//            public void onResult(NodeApi.GetConnectedNodesResult result) {
//                for (int i = 0; i < result.getNodes().size(); i++) {
//                    Node node = result.getNodes().get(i);
//                    String nName = node.getDisplayName();
//                    String nId = node.getId();
//                    Log.d(TAG, "Node name and ID: " + nName + " | " + nId);
//
//                    Wearable.MessageApi.addListener(googleApiClient, new MessageApi.MessageListener() {
//                        @Override
//                        public void onMessageReceived(MessageEvent messageEvent) {
//                            Log.d(TAG, "Message received: " + messageEvent);
//                        }
//                    });
//
//                    PendingResult<MessageApi.SendMessageResult> messageResult = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(),
//                            PATH, null);
//                    messageResult.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
//                        @Override
//                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
//                            Status status = sendMessageResult.getStatus();
//                            Log.d(TAG, "Status: " + status.toString());
////                            if (status.getStatusCode() != WearableStatusCodes.SUCCESS) {
////                                alertButton.setProgress(-1);
////                                label.setText("Tap to retry. Alert not sent :(");
////                            }
//                        }
//                    });
//                }
//            }
//        });
//    }
}
