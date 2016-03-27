package edu.uml.cs.mstowell.wrkr.service;

import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import edu.uml.cs.mstowell.wrkr.ui.MyStubBroadcastActivity;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Dispatches events based on the message received from the wrkr mobile app
 */
public class MobileListenerService extends WearableListenerService implements Globals {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d("wrkr", "ABCDE GOT A MESSAGE FROM THE MOBILE WOOOOOOOO");
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrateStart = {0, 400, 30, 200};
        long[] vibrateStop = {0, 200, 25, 200, 25, 200};
        final int indexInPatternToRepeat = -1;

        switch (messageEvent.getPath()) {
            case MSG_INIT_FROM_DEVICE:
            case MSG_WRIST_EXER_TIME:
                Intent intent = new Intent( this, MyStubBroadcastActivity.class );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case MSG_START_ACCEL:
                vibrator.vibrate(vibrateStart, indexInPatternToRepeat);
                startService(new Intent(MobileListenerService.this, WristTrackingService.class));
                break;

            case MSG_STOP_ACCEL:
                stopService(new Intent(MobileListenerService.this, WristTrackingService.class));
                vibrator.vibrate(vibrateStop, indexInPatternToRepeat);
                break;

            default:
                // unintended message - return to superclass call
                super.onMessageReceived(messageEvent);
                break;
        }

        return;
    }
}