package edu.uml.cs.mstowell.wrkr.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.uml.cs.mstowell.wrkr.MainActivity;
import edu.uml.cs.mstowell.wrkr.R;
import edu.uml.cs.mstowell.wrkr.RestAPI;
import edu.uml.cs.mstowell.wrkr.list.MListAdapter;
import edu.uml.cs.mstowell.wrkr.list.SwipeDismissListViewTouchListener;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;
import edu.uml.cs.mstowell.wrkrlib.common.User;

/**
 * Fragment to provide the notification list to the user
 */
public class HomeFragment extends Fragment implements Globals {

    View root, v;
    ListView lv;
    MListAdapter adapter;
    TextView noNotif, karmaTxt;
    Context mContext;

    public static String[][] dataList={
            {"Wrist exercise due","Inactive for 32 minutes","Open the wrkr website to perform your exercise"},
            {"Wrist exercise complete!","Completed on 03/22/2016 at 02:10PM",""},
            {"Wrist exercise due","Inactive for 23 minutes","Open the wrkr website to perform your exercise"}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = MainActivity.mContext;
        root = v.findViewById(R.id.home_fragment_root);
        noNotif = (TextView) v.findViewById(R.id.home_no_notif);
        karmaTxt = (TextView) v.findViewById(R.id.home_karma);

        noNotif.setVisibility(View.GONE);
        new GetNotificationListTask().execute();

        return v;
    }

    private void setNotificationTextVisibility() {

        // if the data list is empty, display a "No Notifications" text
        if (dataList.length == 0)
            noNotif.setVisibility(View.VISIBLE);
        else
            noNotif.setVisibility(View.GONE);
    }

    private void setNotificationTextVisibility(int adapterDataLength) {

        // if the data list is empty, display a "No Notifications" text
        if (adapterDataLength == 0)
            noNotif.setVisibility(View.VISIBLE);
        else
            noNotif.setVisibility(View.GONE);
    }

    private void getNotificationList() {

        ArrayList<String[]> a = new ArrayList<>();
        SharedPreferences prefs = mContext.getSharedPreferences(GLOBAL_PREFS, 0);
        int uid = prefs.getInt(USER_ID, -1);
        String uname = prefs.getString(USER_EMAIL, "");

        // first get the user's karma score
        if (uname.equals("")) {
            setKarmaText("Karma Score: <not available>");
        } else {
            User u = RestAPI.getUser(uname);
            if (u == null) {
                setKarmaText("Karma Score: <not available>");
            } else {
                String karmaStr;
                if (u.karma < 60) { // 59 and under = bad karma
                    karmaStr = "<font color='#FF2929'>" + u.karma + "</font>";
                } else if (u.karma < 100) { // 60 - 100 = okay karma
                    karmaStr = "<font color='#D6D145'>" + u.karma + "</font>";
                } else { // 100+ = great karma
                    karmaStr = "<font color='#4ADB25'>" + u.karma + "</font>";
                }
                setKarmaText("Karma Score: " + karmaStr);
            }
        }

        // get outstanding user exercises
        if (uid != -1) {

            User u = RestAPI.getExercises(uid);
            if (u == null) {
                promptUserSetupProfile();
            } else if (u.exercises > 0) {

                int exercises = u.exercises;
                JSONArray timestamps = u.timestamps;

                for (int i = 0; i < timestamps.length(); i++) {

                    try {
                        long timestamp = timestamps.getLong(i);

                        Date date = new java.util.Date(timestamp);
                        String timeStr = new SimpleDateFormat("hh:mm, yyyy-MM-dd").format(date);

                        String timeRemaining = getTimeLeft(new Date(timestamp));

                        String[] row = {
                                "Wrist exercise needed " + timeRemaining,
                                "This exercise was recorded at " + timeStr,
                                exercises + " total exercises due"
                        };
                        a.add(row);
                    } catch (JSONException e) {
                        Log.e("wrkr", "Error while parsing timestamps for exercises");
                        e.printStackTrace();
                    }
                }
            }
        } else {
            promptUserSetupProfile();
        }

        // set up the notification list
        dataList = new String[a.size()][];
        int i = 0;
        for (String[] row : a) {
            dataList[i++] = row;
        }
    }

    private void setKarmaText(final String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                karmaTxt.setText(Html.fromHtml(text));
            }
        });
    }

    private void promptUserSetupProfile() {
        // TODO - implement
    }

    // this method thanks to: http://stackoverflow.com/questions/1555262/calculating-the-
    //   difference-between-two-java-date-instances
    @SuppressWarnings("NumericOverflow")
    public static String getTimeLeft(Date dateTime) {

        long timeDifferenceMilliseconds = new Date().getTime() - dateTime.getTime();
        long diffSeconds = timeDifferenceMilliseconds / 1000;
        long diffMinutes = timeDifferenceMilliseconds / (60 * 1000);
        long diffHours = timeDifferenceMilliseconds / (60 * 60 * 1000);
        long diffDays = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24);
        long diffWeeks = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 7);

        if (diffSeconds < 1) {
            return "one second ago";
        } else if (diffMinutes < 1) {
            return diffSeconds + " seconds ago";
        } else if (diffHours < 1) {
            return diffMinutes + " minutes ago";
        } else if (diffDays < 1) {
            return diffHours + " hours ago";
        } else if (diffWeeks < 1) {
            return diffDays + " days ago";
        } else {
            return diffWeeks + (diffWeeks == 1 ? " week ago" : " weeks ago");
        }
    }

    private class GetNotificationListTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog load;

        @Override
        protected void onPreExecute() {
            load = new ProgressDialog(getActivity());
            load.setMessage("Loading ...");
            load.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getNotificationList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setNotificationTextVisibility();
            if (load != null) load.dismiss();

            adapter = new MListAdapter((MainActivity) mContext, dataList);
            lv = (ListView) v.findViewById(R.id.fh_listview);
            lv.setAdapter(adapter);

            SwipeDismissListViewTouchListener touchListener =
                    new SwipeDismissListViewTouchListener(
                            lv,
                            new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                @Override
                                public boolean canDismiss(int position) {
                                    return true;
                                }
                                public void onDismiss(ListView listView, int[] reverseSortedPositions) {

                                    final int position = reverseSortedPositions[0];
                                    final ArrayList<String> item = adapter.remove(position);
                                    adapter.notifyDataSetChanged();
                                    setNotificationTextVisibility(adapter.getData().size());

                                    Snackbar snackbar = Snackbar
                                            .make(root, "Deleted notification", Snackbar.LENGTH_LONG)
                                            .setAction("UNDO", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    adapter.insertToPosition(item, position);
                                                    adapter.notifyDataSetChanged();
                                                    setNotificationTextVisibility(
                                                            adapter.getData().size());

                                                    Snackbar snackbar1 = Snackbar.make(root,
                                                            "Restored notification",
                                                            Snackbar.LENGTH_SHORT);
                                                    snackbar1.show();
                                                }
                                            });
                                    snackbar.show();
                                }
                            });
            lv.setOnTouchListener(touchListener);
            lv.setOnScrollListener(touchListener.makeScrollListener());

            super.onPostExecute(aVoid);
        }
    }
}