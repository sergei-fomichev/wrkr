package edu.uml.cs.mstowell.wrkr;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Mike on 3/13/2016.
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