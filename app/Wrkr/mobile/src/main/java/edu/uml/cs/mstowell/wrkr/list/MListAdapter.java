package edu.uml.cs.mstowell.wrkr.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import edu.uml.cs.mstowell.wrkr.MainActivity;
import edu.uml.cs.mstowell.wrkr.R;

/**
 * List adapter for the HomeFragment notification list.
 */
public class MListAdapter extends BaseAdapter {
    ArrayList<ArrayList<String>> data;
    Context context;

    private static LayoutInflater inflater=null;
    public MListAdapter(MainActivity mainActivity, String[][] mData) {

        context = mainActivity;
        data = new ArrayList<>();

        for (String[] array : mData) {
            ArrayList<String> subData = new ArrayList<>();
            Collections.addAll(subData, array);
            data.add(subData);
        }

        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView title;
        TextView subtitle;
        TextView detail;
    }

    public ArrayList<ArrayList<String>> getData() {
        return data;
    }

    public ArrayList<String> remove(int position) {
        return data.remove(position);
    }

    public void insertToPosition(ArrayList<String> item, int position) {
        data.add(position, item);
    }

    public void insert(String sTitle, String sSubtitle, String sDetail) {
        ArrayList<String> newRow = new ArrayList<>();

        newRow.add(sTitle);
        newRow.add(sSubtitle);
        newRow.add(sDetail);

        data.add(newRow);
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //if (convertView == null) {
        LayoutInflater vi = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = vi.inflate(R.layout.list_item, parent, false);
        //}

        Holder holder = new Holder();
        ArrayList<String> thisData = data.get(position);

        holder.title = (TextView) convertView.findViewById(R.id.list_item_title);
        holder.title.setText(thisData.get(0));

        holder.subtitle = (TextView) convertView.findViewById(R.id.list_item_subtitle);
        holder.subtitle.setText(thisData.get(1));

        holder.detail = (TextView) convertView.findViewById(R.id.list_item_detail);
        holder.detail.setText(thisData.get(2));

        if (holder.detail.getText().length() == 0) {
            holder.detail.setVisibility(View.GONE);
            holder.subtitle.setPadding(holder.subtitle.getPaddingLeft(),
                    holder.subtitle.getPaddingTop(), holder.subtitle.getPaddingRight(),
                    (int) context.getResources().getDimension(R.dimen.list_item_bottom));
        } else {
            holder.detail.setVisibility(View.VISIBLE);
            holder.subtitle.setPadding(holder.subtitle.getPaddingLeft(),
                    holder.subtitle.getPaddingTop(), holder.subtitle.getPaddingRight(),
                    (int) context.getResources().getDimension(R.dimen.list_item_middle));
        }

        return convertView;
    }
}
