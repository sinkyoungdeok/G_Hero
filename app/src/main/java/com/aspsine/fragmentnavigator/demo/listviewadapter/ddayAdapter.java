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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

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
        View itemView = null;
        itemView = inflater.inflate(R.layout.dday_item, parent, false);
        ddayListviewitem listviewitem = data.get(position);

        //ImageView icon = (ImageView)convertView.findViewById(R.id.imageview);
        //icon.setImageResource(listviewitem.getIcon());

        TextView name = (TextView)itemView.findViewById(R.id.textview);
        name.setText(listviewitem.getName());

        TextView name2 = (TextView)itemView.findViewById(R.id.textview2);
        name2.setText(listviewitem.getName2());

        ImageView iv = itemView.findViewById(R.id.iv);

        Glide.with(itemView).load(listviewitem.getIcon()).into(iv);



        return itemView;
    }

}
