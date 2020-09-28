package com.aspsine.fragmentnavigator.demo.listviewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.item.ddayListviewitem;

import java.util.ArrayList;

public class ddayAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<ddayListviewitem> data;
    private int layout;

    public ddayAdapter(Context context, int layout, ArrayList<ddayListviewitem> data) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
    }

    @Override
    public int getCount() { return data.size(); }

    @Override
    public String getItem(int position) { return data.get(position).getName(); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }
        ddayListviewitem listviewitem = data.get(position);

        ImageView icon = (ImageView)convertView.findViewById(R.id.imageview);
        //icon.setImageResource(listviewitem.getIcon());

        TextView name = (TextView)convertView.findViewById(R.id.textview);
        name.setText(listviewitem.getName());

        TextView name2 = (TextView)convertView.findViewById(R.id.textview2);
        name2.setText(listviewitem.getName2());

        convertView.setBackgroundResource(listviewitem.getIcon());



        return convertView;
    }

}
