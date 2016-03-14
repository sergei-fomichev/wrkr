package edu.uml.cs.mstowell.wrkr.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.uml.cs.mstowell.wrkr.MainActivity;
import edu.uml.cs.mstowell.wrkr.R;
import edu.uml.cs.mstowell.wrkr.data.Globals;

/**
 * Created by Mike on 3/1/2016.
 */
public class SettingsFragment extends Fragment implements Globals {

    private Button sendMsg;
    private static TextView wearDebug;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        wearDebug = (TextView) v.findViewById(R.id.setting_debug_info);

        sendMsg = (Button) v.findViewById(R.id.settings_send_notif);
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).sendMessage(MSG_WRIST_EXER_TIME, "");
                Log.d("wrkr", "ABCDE Message sent to wear device");
            }
        });

        return v;
    }

    public static class WearListenerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (wearDebug != null) {

                String event = intent.getStringExtra(WEAR_DATA_KEY);
                byte[] rawData = intent.getByteArrayExtra(WEAR_DATA_VALUES);
                String data = new String(rawData);

                if (event == null) event = "ERROR";
                wearDebug.setText("From wear: " + event + " --- " + data);
            }
        }
    }
}
