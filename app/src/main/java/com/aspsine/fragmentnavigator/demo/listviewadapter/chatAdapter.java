package com.aspsine.fragmentnavigator.demo.listviewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.item.chatListviewitem;
import com.aspsine.fragmentnavigator.demo.item.ddayListviewitem;

import java.util.ArrayList;

public class chatAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<chatListviewitem> data;
    private int layout;

    public chatAdapter(Context context, int layout, ArrayList<chatListviewitem> data) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
    }

    @Override
    public int getCount() { return data.size(); }

    @Override
    public String getItem(int position) { return data.get(position).getContent(); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }
        chatListviewitem listviewitem = data.get(position);
        ImageView icon = (ImageView) convertView.findViewById(R.id.profile);
        TextView content = (TextView) convertView.findViewById(R.id.content);
        TextView content2 = (TextView) convertView.findViewById(R.id.content2);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView date2 = (TextView) convertView.findViewById(R.id.date2);
        if(listviewitem.getMEorYOUR()) {
            icon.setVisibility(View.INVISIBLE);
            content.setVisibility(View.INVISIBLE);
            content2.setText(listviewitem.getContent());
            date.setVisibility(View.INVISIBLE);
            date2.setText(listviewitem.getDate());
        } else {
            icon.setImageResource(listviewitem.getIcon());
            content.setText(listviewitem.getContent());
            content2.setVisibility(View.INVISIBLE);
            date.setText(listviewitem.getDate());
            date2.setVisibility(View.INVISIBLE);
        }



        return convertView;
    }

}
