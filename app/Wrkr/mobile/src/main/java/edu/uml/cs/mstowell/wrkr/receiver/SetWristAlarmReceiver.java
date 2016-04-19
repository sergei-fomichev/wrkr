package edu.uml.cs.mstowell.wrkr.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * BroadcastReceiver run both on app start and whenever the device boots to ensure
 * that the user's wrist motions are tracked every weekday between 8am and 4pm
 */
public class SetWristAlarmReceiver extends BroadcastReceiver implements Globals {

    @Override
    public void onReceive(Context context, final Intent intent) {

        setStartTimer(context);
        setStopTimer(context);
    }

    private void setStartTimer(Context context) {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        int thisHour = calendar.get(Calendar.HOUR_OF_DAY);
        int thisMinute = calendar.get(Calendar.MINUTE);

        // if it is Friday after the stop trigger period, skip and set the alarm
        // for next Monday (we do not want weekends).  If we are between the start
        // and stop period, note the AlarmManager will trigger immediately
        long triggerAt = calendar.getTimeInMillis();
        if (today == Calendar.FRIDAY) {
            Log.d("wrkr", "Calendar -- today is friday, skipping 3 days");
            triggerAt += (AlarmManager.INTERVAL_DAY * 3);
        } else if (thisHour > STOP_TRACKING_HOUR && thisMinute > STOP_TRACKING_MINUTE) {
            Log.d("wrkr", "Calendar -- passed the stop hour .. skipping 1 day");
            triggerAt += (AlarmManager.INTERVAL_DAY);
        }

        calendar.set(Calendar.HOUR_OF_DAY, START_TRACKING_HOUR);
        calendar.set(Calendar.MINUTE, START_TRACKING_MINUTE);
        calendar.set(Calendar.SECOND, 0);

        Log.d("wrkr", "Calendar -- trigger at day " + calendar.get(Calendar.DAY_OF_WEEK) +
                ", " + calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));

        PendingIntent pi = PendingIntent.getBroadcast(context, 0,
                new Intent(context, StartWristTrackingReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAt, AlarmManager.INTERVAL_DAY, pi);
    }

    private void setStopTimer(Context context) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, STOP_TRACKING_HOUR);
        calendar.set(Calendar.MINUTE, STOP_TRACKING_MINUTE);
        calendar.set(Calendar.SECOND, 0);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0,
                new Intent(context, StopWristTrackingReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
    }
}
