package edu.uml.cs.mstowell.wrkr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Mike on 3/2/2016.
 */
public class MListAdapter extends BaseAdapter {
    ArrayList<String> data;
    Context context;

    private static LayoutInflater inflater=null;
    public MListAdapter(MainActivity mainActivity, String[] mData) {

        context = mainActivity;
        data = new ArrayList<String>();
        Collections.addAll(data, mData);

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
        TextView tv;
    }

    public String remove(int position) {
        return data.remove(position);
    }

    public void insertToPosition(String item, int position) {
        data.add(position, item);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView;
        rowView = inflater.inflate(R.layout.list_item, parent, false);

        Holder holder = new Holder();
        holder.tv = (TextView) rowView.findViewById(R.id.list_item_text);
        holder.tv.setText(data.get(position));

        return rowView;
    }
}
