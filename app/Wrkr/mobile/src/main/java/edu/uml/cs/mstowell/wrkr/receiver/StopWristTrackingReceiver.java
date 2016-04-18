package edu.uml.cs.mstowell.wrkr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.uml.cs.mstowell.wrkr.service.RecordDataService;
import edu.uml.cs.mstowell.wrkrlib.common.APIClientCommon;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Stops the wrist tracking
 */
public class StopWristTrackingReceiver extends BroadcastReceiver implements Globals {


    @Override
    public void onReceive(Context context, Intent intent) {

        // stop the data recording service
        Intent serviceIntent = new Intent(context, RecordDataService.class);
        context.stopService(serviceIntent);

        // tell wear to stop recording data
        APIClientCommon mApiClient = new APIClientCommon(context);
        mApiClient.sendMessage(MSG_STOP_ACCEL, "");
    }
}
