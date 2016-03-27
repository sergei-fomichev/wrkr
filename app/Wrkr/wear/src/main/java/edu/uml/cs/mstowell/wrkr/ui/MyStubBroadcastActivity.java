package edu.uml.cs.mstowell.wrkr.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Example shell activity which simply broadcasts to the receiver and exits
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
