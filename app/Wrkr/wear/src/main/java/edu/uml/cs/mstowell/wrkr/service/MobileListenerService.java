package edu.uml.cs.mstowell.wrkr.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Vibrator;

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
    long[] vibrateStart = {0, 400, 30, 200};         //
    long[] vibrateStop = {0, 200, 25, 200, 25, 200}; // different vibration patterns
    long[] vibrateExercise = {0, 300, 50, 600};      //

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        final int indexInPatternToRepeat = -1;
        String vibrateStr = new String(messageEvent.getData());

        switch (messageEvent.getPath()) {
            case MSG_INIT_FROM_DEVICE:
            case MSG_WRIST_EXER_TIME:
                // display notification to user that an exercise is due
                Intent intent = new Intent(this, MyStubBroadcastActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                vibrator.vibrate(vibrateExercise, indexInPatternToRepeat);
                startActivity(intent);
                break;

            case MSG_START_ACCEL:
                // start the accelerometer data collection service
                if (!vibrateStr.equals(DO_NOT_VIBRATE))
                    vibrator.vibrate(vibrateStart, indexInPatternToRepeat);
                startRecordingData();
                break;

            case MSG_STOP_ACCEL:
                // stop the accelerometer data collection service
                stopRecordingData();
                if (!vibrateStr.equals(DO_NOT_VIBRATE))
                    vibrator.vibrate(vibrateStop, indexInPatternToRepeat);
                break;

            default:
                // unintended message - return to superclass call
                super.onMessageReceived(messageEvent);
                break;
        }
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