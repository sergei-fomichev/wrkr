package edu.uml.cs.mstowell.wrkr.service;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import edu.uml.cs.mstowell.wrkr.ui.MyStubBroadcastActivity;

/**
 * Created by Mike on 3/12/2016.
 */
public class MobileListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        // TODO - check the message ID to make sure its an expected string --
        // TODO - otherwise, call super.OnMessageReceived

        Log.d("wrkr", "ABCDE GOT A MESSAGE FROM THE MOBILE WOOOOOOOO");
        Intent intent = new Intent( this, MyStubBroadcastActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //super.onMessageReceived(messageEvent);
    }
}