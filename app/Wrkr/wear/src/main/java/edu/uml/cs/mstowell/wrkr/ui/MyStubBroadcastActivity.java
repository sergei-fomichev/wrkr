package edu.uml.cs.mstowell.wrkr.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Shell activity which simply broadcasts to the notification receiver and exits.
 * This is the chain that starts showing the notification
 */
public class MyStubBroadcastActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = new Intent();
        i.setAction("edu.uml.cs.mstowell.wrkr.SHOW_NOTIFICATION");
        i.putExtra(MyPostNotificationReceiver.CONTENT_KEY, "Wrist exercise due");
        sendBroadcast(i);
        finish();
    }
}
