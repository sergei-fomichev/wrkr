package edu.uml.cs.mstowell.wrkr.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.uml.cs.mstowell.wrkr.R;
import edu.uml.cs.mstowell.wrkr.service.RecordDataService;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Debug information available for the user
 */
public class SettingsFragment extends Fragment implements Globals {

    private static TextView wearDebug;
    private SettingsFragReceiver sfr;

    private static class SettingsFragReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (wearDebug != null) {
                Bundle bundleData = intent.getBundleExtra(SETTINGS_FRAG_BUNDLE);
                String event = bundleData.getString(WEAR_DATA_KEY, "");
                String data = bundleData.getString(WEAR_DATA_VALUES, "");

                if (event == null) event = "ERROR";
                wearDebug.setText(Html.fromHtml("From wear:<br/>Event: "
                        + event + "<br/>Data: " + data));

                /*if (event.equals(MSG_WEAR_DATA)) {
                    writeDataCSV(data);
                }*/
            }
        }
    }

    @Override
    public void onDestroy() {

        // unregister the broadcast receiver
        getActivity().unregisterReceiver(sfr);

        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        wearDebug = (TextView) v.findViewById(R.id.setting_debug_info);

        // register the wrist tracking receiver
        sfr = new SettingsFragReceiver();
        IntentFilter intentFilter = new IntentFilter(SETTINGS_RCV_ACTION);
        getActivity().registerReceiver(sfr, intentFilter);

        Button pingWear = (Button) v.findViewById(R.id.settings_send_notif);
        pingWear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).sendMessage(MSG_WRIST_EXER_TIME, "");
                Log.d("wrkr", "ABCDE Message sent to wear device");
            }
        });

        Button startAccel = (Button) v.findViewById(R.id.settings_start_accel);
        startAccel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent serviceIntent = new Intent(getActivity().getApplicationContext(),
                        RecordDataService.class);
                getActivity().startService(serviceIntent);

                ((MainActivity) getActivity()).sendMessage(MSG_START_ACCEL, "");
                Log.d("wrkr", "ABCDE accel start sent to wear device");
            }
        });

        Button stopAccel = (Button) v.findViewById(R.id.settings_stop_accel);
        stopAccel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent serviceIntent = new Intent(getActivity().getApplicationContext(),
                        RecordDataService.class);
                getActivity().stopService(serviceIntent);

                ((MainActivity) getActivity()).sendMessage(MSG_STOP_ACCEL, "");
                Log.d("wrkr", "ABCDE accel stop sent to wear device");
            }
        });

        return v;
    }

    @SuppressWarnings("unused, all")
    private void writeDataCSV(String data) {

        try {
            JSONObject dataJO = new JSONObject(data);

            File folder = new File(Environment.getExternalStorageDirectory() + "/wrkr");
            File f;
            String fileName;

            if (!folder.exists()) {
                folder.mkdir();
            }

            Log.d("wrkr", "ABCDE about to write a csv");

            fileName = "data_" +
                    new SimpleDateFormat("yyMMdd_HH_mm_ss", Locale.US)
                            .format(Calendar.getInstance().getTime()) + ".csv";
            f = new File(folder, fileName);

            FileWriter gpxwriter = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(gpxwriter);

            out.write("x, y, z, mag, wma\n");

            JSONArray x = dataJO.getJSONArray("x");
            JSONArray y = dataJO.getJSONArray("y");
            JSONArray z = dataJO.getJSONArray("z");
            JSONArray mag = dataJO.getJSONArray("mag");
            JSONArray wma = dataJO.getJSONArray("wma");

            for (int i = 0; i < x.length(); i++) {
                out.append(x.get(i) + "," + y.get(i) + "," + z.get(i) + ","
                    + mag.get(i) + "," + wma.get(i) + "\n");
            }

            out.close();
            gpxwriter.close();

            Log.d("wrkr", "############# ABCDE wrote " + fileName + " ################");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
