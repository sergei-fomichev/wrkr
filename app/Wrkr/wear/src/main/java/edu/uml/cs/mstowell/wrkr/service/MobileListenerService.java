package edu.uml.cs.mstowell.wrkr.service;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import edu.uml.cs.mstowell.wrkr.ui.MyStubBroadcastActivity;
import edu.uml.cs.mstowell.wrkrlib.data.Globals;

/**
 * Created by Mike on 3/12/2016.
 */
public class MobileListenerService extends WearableListenerService implements Globals {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d("wrkr", "ABCDE GOT A MESSAGE FROM THE MOBILE WOOOOOOOO");

        switch (messageEvent.getPath()) {
            case MSG_INIT_FROM_DEVICE:
            case MSG_WRIST_EXER_TIME:
                Intent intent = new Intent( this, MyStubBroadcastActivity.class );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case MSG_START_ACCEL:
                startService(new Intent(MobileListenerService.this, WristTrackingService.class));
                break;

            case MSG_STOP_ACCEL:
                stopService(new Intent(MobileListenerService.this, WristTrackingService.class));
                break;

            default:
                // unintended message - return to superclass call
                super.onMessageReceived(messageEvent);
                break;
        }

        return;
    }
}