package edu.uml.cs.mstowell.wrkr.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import edu.uml.cs.mstowell.wrkr.R;
import edu.uml.cs.mstowell.wrkrlib.common.APIClientCommon;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Notification UI
 */
public class MyDisplayActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, Globals {

    private APIClientCommon mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        // received a message since we're here, init a GoogleAPIClient and respond
        mApiClient = new APIClientCommon(this);
        mApiClient.sendMessage(MSG_WEAR_MSG_ACK, "notification received");
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
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("wrkr", "wear disconnected from mobile device");
    }
}
