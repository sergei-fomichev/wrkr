package edu.uml.cs.mstowell.wrkr.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import edu.uml.cs.mstowell.wrkr.list.MListAdapter;
import edu.uml.cs.mstowell.wrkr.MainActivity;
import edu.uml.cs.mstowell.wrkr.R;
import edu.uml.cs.mstowell.wrkr.list.SwipeDismissListViewTouchListener;

/**
 * Fragment to provide the notification list to the user
 */
public class HomeFragment extends Fragment {

    View root;
    ListView lv;
    MListAdapter adapter;
    Context context;

    public static String[][] dataList={
            {"Wrist exercise due","Inactive for 32 minutes","Open the wrkr website to perform your exercise"},
            {"Wrist exercise complete!","Completed on 03/22/2016 at 02:10PM",""},
            {"Wrist exercise due","Inactive for 23 minutes","Open the wrkr website to perform your exercise"}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        context = MainActivity.mContext;
        root = v.findViewById(R.id.home_fragment_root);

        adapter = new MListAdapter((MainActivity)context, dataList);
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

                                Snackbar snackbar = Snackbar
                                        .make(root, "Deleted notification", Snackbar.LENGTH_LONG)
                                        .setAction("UNDO", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                adapter.insertToPosition(item, position);
                                                adapter.notifyDataSetChanged();

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

        return v;
    }
}