package com.aspsine.fragmentnavigator.demo.listviewadapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.item.chatListviewitem;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

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
    public View getView(int position, View view, ViewGroup viewGroup) {
        chatListviewitem listviewitem = data.get(position);
        View itemView = null;

        if(listviewitem.getMEorYOUR()) {
            itemView = inflater.inflate(R.layout.contact_item, viewGroup, false);
        } else {
            itemView = inflater.inflate(R.layout.contact_item_your, viewGroup, false);
        }
        CircleImageView iv = itemView.findViewById(R.id.iv);
        TextView tvName = itemView.findViewById(R.id.tv_name);
        TextView tvMsg = itemView.findViewById(R.id.tv_msg);
        TextView tvTime = itemView.findViewById(R.id.tv_time);
        TextView tvRead = itemView.findViewById(R.id.tv_read);


        if(listviewitem.getMEorYOUR() && listviewitem.getRead()) { // 읽었을때에는 1표시 없애기
            tvRead.setVisibility(View.INVISIBLE);
        }

        tvName.setText(listviewitem.getName());
        tvMsg.setText(listviewitem.getContent());
        tvTime.setText(listviewitem.getDate());

        Glide.with(itemView).load(listviewitem.getIconPath()).into(iv);
        if(listviewitem.getPrevName() != null) {
            if (listviewitem.getPrevName().equals(listviewitem.getName())) {
                if (listviewitem.getPrevDate().equals(listviewitem.getDate())) {
                    tvName.setVisibility(View.INVISIBLE);
                    iv.setVisibility(View.INVISIBLE);
                    tvTime.setVisibility(View.INVISIBLE);
                }
            }
        }

        return itemView;
    }

}
