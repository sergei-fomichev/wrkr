package edu.uml.cs.mstowell.wrkr;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Mike on 3/1/2016.
 */
public class HomeFragment extends Fragment {

    View root;
    ListView lv;
    MListAdapter adapter;
    Context context;

    public static String [] dataList={"1","2","3","4","5","6","7","8",
            "9","10","11","12","13","14","15","16",};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        context = MainActivity.mContext;
        root = (View) v.findViewById(R.id.home_fragment_root);

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
                                final String item = adapter.remove(position);
                                adapter.notifyDataSetChanged();

                                Snackbar snackbar = Snackbar
                                        .make(root, "Deleted \"" + item + "\"", Snackbar.LENGTH_LONG)
                                        .setAction("UNDO", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                adapter.insertToPosition(item, position);
                                                adapter.notifyDataSetChanged();

                                                Snackbar snackbar1 = Snackbar.make(root, "Restored", Snackbar.LENGTH_SHORT);
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