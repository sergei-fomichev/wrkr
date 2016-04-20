package edu.uml.cs.mstowell.wrkr.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import edu.uml.cs.mstowell.wrkr.service.RecordDataService;
import edu.uml.cs.mstowell.wrkrlib.common.APIClientCommon;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Stops the wrist tracking
 */
public class StopWristTrackingReceiver extends BroadcastReceiver implements Globals {


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("wrkr", "StopWristTrackingReceiver called");

        // stop the data recording service
        Intent serviceIntent = new Intent(context, RecordDataService.class);
        context.stopService(serviceIntent);

        // tell wear to stop recording data
        APIClientCommon mApiClient = new APIClientCommon(context);
        mApiClient.sendMessage(MSG_STOP_ACCEL, "");

        // set the next starting alarm
        setStartTimer(context);
    }

    private void setStartTimer(Context context) {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        // bring back calendar to the start time TODO - probably unnecessary
        //calendar.add(Calendar.HOUR_OF_DAY, -(STOP_TRACKING_HOUR - START_TRACKING_HOUR));
        //calendar.add(Calendar.HOUR_OF_DAY, -(STOP_TRACKING_MINUTE - START_TRACKING_MINUTE));

        calendar.set(Calendar.HOUR_OF_DAY, START_TRACKING_HOUR);
        calendar.set(Calendar.MINUTE, START_TRACKING_MINUTE);
        calendar.set(Calendar.SECOND, 0);

        // if it is Friday, skip 3 days (to Monday), otherwise skip one day
        long triggerAt = (today == Calendar.FRIDAY
                ? calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY * 3)
                : calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY));

        Log.d("wrkr", "Scheduling next time to start wrist service");

        PendingIntent pi = PendingIntent.getBroadcast(context, 0,
                new Intent(context, StartWristTrackingReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAt, AlarmManager.INTERVAL_DAY, pi);
    }
}
