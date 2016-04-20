package edu.uml.cs.mstowell.wrkr.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Date;

import edu.uml.cs.mstowell.wrkr.ui.MyStubBroadcastActivity;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Dispatches events based on the message received from the wrkr mobile app
 */
public class MobileListenerService extends WearableListenerService implements Globals {

    private int INTENT_REQUEST_CODE = 17313;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d("wrkr", "ABCDE GOT A MESSAGE FROM THE MOBILE WOO");

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrateStart = {0, 400, 30, 200};
        long[] vibrateStop = {0, 200, 25, 200, 25, 200};
        long[] vibrateExercise = {0, 300, 50, 600};
        final int indexInPatternToRepeat = -1;

        switch (messageEvent.getPath()) {
            case MSG_INIT_FROM_DEVICE:
            case MSG_WRIST_EXER_TIME:
                Intent intent = new Intent(this, MyStubBroadcastActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                vibrator.vibrate(vibrateExercise, indexInPatternToRepeat);
                startActivity(intent);
                break;

            case MSG_START_ACCEL:
                vibrator.vibrate(vibrateStart, indexInPatternToRepeat);
                startRecordingData();
                break;

            case MSG_STOP_ACCEL:
                stopRecordingData();
                String vibrateStr = new String(messageEvent.getData());
                if (!vibrateStr.equals(DO_NOT_VIBRATE))
                    vibrator.vibrate(vibrateStop, indexInPatternToRepeat);
                break;

            default:
                // unintended message - return to superclass call
                super.onMessageReceived(messageEvent);
                break;
        }

        return;
    }

    public void startRecordingData() {

        Intent intent = new Intent(this, WristTrackingService.class);
        PendingIntent wristServiceIntent = PendingIntent.getService(
                this, INTENT_REQUEST_CODE, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Date now = new Date();

        // restart service every 60 seconds in case it gets killed by the system
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now.getTime(),
                60 * 1000, wristServiceIntent);
    }

    public void stopRecordingData() {

        // cancel the alarm manager
        Intent intent = new Intent(this, WristTrackingService.class);
        PendingIntent wristServiceIntent = PendingIntent.getService(
                this, INTENT_REQUEST_CODE, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.cancel(wristServiceIntent);

        // stop the service
        stopService(new Intent(this, WristTrackingService.class));
    }
}