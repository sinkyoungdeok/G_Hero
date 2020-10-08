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
    //private TextView tvPrevTime;

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
        System.out.println(listviewitem.getContent()+","+listviewitem.getPrevName()+","+listviewitem.getName() + ",," + listviewitem.getPrevDate() + "," + listviewitem.getDate() );
        if(listviewitem.getMEorYOUR()) {
            if(listviewitem.getPrevName() != null && listviewitem.getPrevName().equals(listviewitem.getName()) && listviewitem.getPrevDate().equals(listviewitem.getDate())){
                System.out.println("test1");
                itemView = inflater.inflate(R.layout.contact_item_duplicate, viewGroup, false);
                TextView tvMsg = itemView.findViewById(R.id.tv_msg);
                TextView tvRead = itemView.findViewById(R.id.tv_read);
                TextView tvTime = itemView.findViewById(R.id.tv_time);
                //tvPrevTime.setVisibility(View.INVISIBLE);
                tvTime.setText(listviewitem.getDate());
                if (listviewitem.getMEorYOUR() && listviewitem.getRead()) { // 읽었을때에는 1표시 없애기
                    tvRead.setVisibility(View.INVISIBLE);
                }
                tvMsg.setText(listviewitem.getContent());
                //tvPrevTime = tvTime;
            } else {
                System.out.println("test2");
                itemView = inflater.inflate(R.layout.contact_item, viewGroup, false);
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
                //tvPrevTime = tvTime;
            }
        } else {
            if(listviewitem.getPrevName() != null && listviewitem.getPrevName().equals(listviewitem.getName()) && listviewitem.getPrevDate().equals(listviewitem.getDate())){
                System.out.println("test3");
                itemView = inflater.inflate(R.layout.contact_item_your_duplicate, viewGroup, false);
                TextView tvMsg = itemView.findViewById(R.id.tv_msg);
                TextView tvRead = itemView.findViewById(R.id.tv_read);
                TextView tvTime = itemView.findViewById(R.id.tv_time);
                //tvPrevTime.setVisibility(View.INVISIBLE);
                tvTime.setText(listviewitem.getDate());
                if (listviewitem.getMEorYOUR() && listviewitem.getRead()) { // 읽었을때에는 1표시 없애기
                    tvRead.setVisibility(View.INVISIBLE);
                }
                tvMsg.setText(listviewitem.getContent());
                //tvPrevTime = tvTime;
            } else {
                System.out.println("test4");
                itemView = inflater.inflate(R.layout.contact_item_your, viewGroup, false);
                CircleImageView iv = itemView.findViewById(R.id.iv);
                TextView tvName = itemView.findViewById(R.id.tv_name);
                TextView tvMsg = itemView.findViewById(R.id.tv_msg);
                TextView tvTime = itemView.findViewById(R.id.tv_time);
                TextView tvRead = itemView.findViewById(R.id.tv_read);


                if (listviewitem.getMEorYOUR() && listviewitem.getRead()) { // 읽었을때에는 1표시 없애기
                    tvRead.setVisibility(View.INVISIBLE);
                }

                tvName.setText(listviewitem.getName());
                tvMsg.setText(listviewitem.getContent());
                tvTime.setText(listviewitem.getDate());

                Glide.with(itemView).load(listviewitem.getIconPath()).into(iv);
                //tvPrevTime = tvTime;
            }
        }

        return itemView;
    }

}
