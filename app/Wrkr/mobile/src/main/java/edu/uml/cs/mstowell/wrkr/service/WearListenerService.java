package edu.uml.cs.mstowell.wrkr.service;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Dispatches events based on the message received from the wrkr wear app
 */
public class WearListenerService extends WearableListenerService implements Globals {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        Log.d("wrkr", "ABCDE WearListenerService got data from wear");

        Intent intent = new Intent();
        intent.setAction("edu.uml.cs.mstowell.WEAR_MSG_RCV_BROADCAST");
        intent.putExtra(WEAR_DATA_KEY, messageEvent.getPath());
        intent.putExtra(WEAR_DATA_VALUES, messageEvent.getData());

        // send the wear data to our broadcast receiver for processing
        sendBroadcast(intent);
    }
}