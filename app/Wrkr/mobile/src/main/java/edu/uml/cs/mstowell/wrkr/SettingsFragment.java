package edu.uml.cs.mstowell.wrkr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Mike on 3/1/2016.
 */
public class SettingsFragment extends Fragment {

    private Button sendMsg;
    private TextView wearDebug;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        wearDebug = (TextView) v.findViewById(R.id.setting_debug_info);

        sendMsg = (Button) v.findViewById(R.id.settings_send_notif);
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).sendMessage(Globals.WRIST_EXER_TIME, "hi wear from mobile");
                Log.d("wrkr", "ABCDE Message sent to wear device");
            }
        });

        return v;
    }
}
