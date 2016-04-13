package edu.uml.cs.mstowell.wrkr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import edu.uml.cs.mstowell.wrkr.MainActivity;
import edu.uml.cs.mstowell.wrkr.R;
import edu.uml.cs.mstowell.wrkr.SingletonMessenger;
import edu.uml.cs.mstowell.wrkr.service.RecordDataService;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Debug information available for the user
 */
public class SettingsFragment extends Fragment implements Globals {

    private static TextView wearDebug;
    private static Handler watch2phoneHandler;

    // TODO - probably better off as a broadcast...
    private static class Watch2PhoneHandler extends Handler {
        private final WeakReference<SettingsFragment> mSettingsFrag;

        public Watch2PhoneHandler(SettingsFragment frag) {
            mSettingsFrag = new WeakReference<>(frag);
        }

        @Override
        public void handleMessage(Message msg) {
            SettingsFragment frag = mSettingsFrag.get();
            if (frag != null && wearDebug != null) {
                Bundle bundleData = msg.getData();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        //mContext = MainActivity.mContext;
        wearDebug = (TextView) v.findViewById(R.id.setting_debug_info);
        watch2phoneHandler = new Watch2PhoneHandler(this);

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
                Messenger messenger = SingletonMessenger.getInstance(watch2phoneHandler);
                serviceIntent.putExtra(RECORD_SERVICE_MESSENGER, messenger);
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

    /*
    @Override
    public void onDestroy() {
        // TODO - probably don't want...
        // stop service - this will stop the service and remove the persistent
        // notification if either the user force quits the app or switches to another
        // fragment within the application
        Intent serviceIntent = new Intent(getActivity().getApplicationContext(),
                RecordDataService.class);
        getActivity().stopService(serviceIntent);

        super.onDestroy();
    }*/

    /*private void writeDataCSV(String data) {

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
                    new SimpleDateFormat("yyMMdd_HH_mm_ss")
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
    }*/
}
