package com.aspsine.fragmentnavigator.demo.listviewadapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.item.calendarListviewitem;
import com.aspsine.fragmentnavigator.demo.item.emoticonListviewitem;
import com.aspsine.fragmentnavigator.demo.ui.activity.LoginActivity;
import com.aspsine.fragmentnavigator.demo.ui.activity.SignupActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class emoticonAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<emoticonListviewitem> data;
    private int layout;

    public emoticonAdapter(Context context, int layout, ArrayList<emoticonListviewitem> data) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
    }
    @Override
    public int getCount() { return data.size(); }

    @Override
    public String getItem(int position) { return ""; }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = null;
        itemView =  inflater.inflate(R.layout.emoticon_item, parent, false);
        emoticonListviewitem item = data.get(position);

        ImageView img1 = (ImageView)itemView.findViewById(R.id.itemImg1);
        ImageView img2 = (ImageView)itemView.findViewById(R.id.itemImg2);
        ImageView img3 = (ImageView)itemView.findViewById(R.id.itemImg3);

        img1.setImageResource(item.getIcon1());
        img1.setVisibility(View.VISIBLE);
        img2.setImageResource(item.getIcon2());
        img2.setVisibility(View.VISIBLE);
        img3.setImageResource(item.getIcon3());
        img3.setVisibility(View.VISIBLE);
        View finalItemView = itemView;
        img1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Glide.with(finalItemView.getContext()).load(item.getIcon1()).into(item.getImgview());
            }
        });
        img2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Glide.with(finalItemView.getContext()).load(item.getIcon2()).into(item.getImgview());
            }
        });
        img3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Glide.with(finalItemView.getContext()).load(item.getIcon3()).into(item.getImgview());
            }
        });


        return itemView;
    }

}
