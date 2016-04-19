package edu.uml.cs.mstowell.wrkr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.uml.cs.mstowell.wrkr.service.RecordDataService;
import edu.uml.cs.mstowell.wrkrlib.common.APIClientCommon;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Starts the wrist tracking
 */
public class StartWristTrackingReceiver extends BroadcastReceiver implements Globals {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("wrkr", "StartWristTrackingReceiver called");

        // start the data recording service.  NOTE: debugging will not be enabled
        Intent serviceIntent = new Intent(context, RecordDataService.class);
        context.startService(serviceIntent);

        // tell wear to start recording data
        APIClientCommon mApiClient = new APIClientCommon(context);
        mApiClient.sendMessage(MSG_START_ACCEL, "");
    }
}
