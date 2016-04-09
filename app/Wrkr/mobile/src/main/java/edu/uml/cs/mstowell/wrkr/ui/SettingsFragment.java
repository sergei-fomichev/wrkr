package edu.uml.cs.mstowell.wrkr.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.uml.cs.mstowell.wrkr.MainActivity;
import edu.uml.cs.mstowell.wrkr.R;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;
import edu.uml.cs.mstowell.wrkr.RestAPI;
import edu.uml.cs.mstowell.wrkrlib.common.User;

/**
 * Debug information available for the user
 */
public class SettingsFragment extends Fragment implements Globals {

    private static TextView wearDebug;
    private static Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        mContext = MainActivity.mContext;
        wearDebug = (TextView) v.findViewById(R.id.setting_debug_info);

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
                ((MainActivity) getActivity()).sendMessage(MSG_START_ACCEL, "");
                Log.d("wrkr", "ABCDE accel start sent to wear device");
            }
        });

        Button stopAccel = (Button) v.findViewById(R.id.settings_stop_accel);
        stopAccel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).sendMessage(MSG_STOP_ACCEL, "");
                Log.d("wrkr", "ABCDE accel stop sent to wear device");
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
                wearDebug.setText(Html.fromHtml("From wear:<br/>Event: "
                        + event + "<br/>Data: " + data));

                if (event.equals(MSG_USER_NEEDS_EXERCISE)) {
                    sendUserNeedsExercise(context);
                } else if (event.equals(MSG_WEAR_DATA)) {
                    //writeDataCSV(data);
                }
            }
        }
    }

    private static void sendUserNeedsExercise(Context c) {

        SharedPreferences prefs = c.getSharedPreferences(GLOBAL_PREFS, 0);
        SharedPreferences.Editor edit = prefs.edit();

        int uid = prefs.getInt(USER_ID, -1);
        String strEmail = prefs.getString(USER_EMAIL, "");

        RestAPI.dieNetworkOnMainThreadException();

        if (uid == -1) {
            // need to obtain the uid from the API
            User u = RestAPI.getUser(strEmail);
            if (u == null) {
                // TODO - handle case where user isn't set up in the DB yet
                Log.e("wrkr", "USER IS NULL!");
            } else {
                uid = u.id;
                edit.putInt(USER_ID, uid).apply();
            }
        }

        // send the server that the user has an exercise due
        User u = RestAPI.postExercise(uid, System.currentTimeMillis());
        if (u == null) {
            // TODO - handle this case
            Log.e("wrkr", "USER IS NULL AFTER POST EXERCISE!");
        } else {
            Log.d("wrkr", "ABCDE user " + uid + " has " + u.exercises + " exercise(s) due");
        }

        // send the watch a notification (acts as an ACK)
        ((MainActivity)mContext).sendMessage(MSG_WRIST_EXER_TIME, "");
    }

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
