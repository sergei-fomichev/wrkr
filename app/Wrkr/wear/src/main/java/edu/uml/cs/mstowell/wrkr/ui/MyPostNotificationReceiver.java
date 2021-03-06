package edu.uml.cs.mstowell.wrkr.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.uml.cs.mstowell.wrkr.R;

/**
 * Launches the notification to display to the user
 */
public class MyPostNotificationReceiver extends BroadcastReceiver {
    public static final String CONTENT_KEY = "contentText";

    public MyPostNotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent displayIntent = new Intent(context, MyDisplayActivity.class);
        String text = intent.getStringExtra(CONTENT_KEY);
        Notification notification = new Notification.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(text)
                .extend(new Notification.WearableExtender()
                        .setDisplayIntent(PendingIntent.getActivity(context, 0, displayIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT)))
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        ((NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE)).notify(0, notification);
    }
}
