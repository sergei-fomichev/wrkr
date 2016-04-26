package edu.uml.cs.mstowell.wrkrlib.common;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Used to execute common code regarding the GoogleAPIClient, such as initialization
 * and sending a message.
 */
public class APIClientCommon {

    private GoogleApiClient mApiClient = null;
    private Context mContext;

    public APIClientCommon(Context c) {
        mContext = c;
        if (mApiClient == null)
            initGoogleApiClient();
    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Wearable.API)
                .build();

        if(!(mApiClient.isConnected() || mApiClient.isConnecting()))
            mApiClient.connect();
    }

    public void sendMessage(final String path, final String text) {
        new Thread( new Runnable() {
            @Override
            public void run() {

                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mApiClient).await();

                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();

                    Log.d("wrkr", "sendMessage Result = " + result.getStatus().toString());
                }
            }
        }).start();
    }

    public boolean isConnected() {
        return mApiClient.isConnected();
    }

    public void disconnect() {
        mApiClient.disconnect();
    }

    public void unregisterConnectionCallbacks(GoogleApiClient.ConnectionCallbacks ctxt) {
        mApiClient.unregisterConnectionCallbacks(ctxt);
    }

    public boolean isConnecting() {
        return mApiClient.isConnecting();
    }

    public void connect() {
        mApiClient.connect();
    }

    public boolean areWatchAndPhonePaired() {
        return true;
    }
}
