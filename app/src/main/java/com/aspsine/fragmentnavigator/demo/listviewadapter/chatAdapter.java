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
import com.aspsine.fragmentnavigator.demo.item.ddayListviewitem;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class chatAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<chatListviewitem> data;
    private int layout;
    private FirebaseStorage storage, yourstorage;
    private StorageReference storageReference, yourstorageReference;
    private StorageReference pathReference, yourpathReference;

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
        if(listviewitem.getMEorYOUR()) { // 내가 보낸 채팅
            icon.setVisibility(View.INVISIBLE);
            content.setVisibility(View.INVISIBLE);
            content2.setText(listviewitem.getContent());
            date.setVisibility(View.INVISIBLE);
            date2.setText(listviewitem.getDate());
        } else { // 상대가 보낸 채팅
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReferenceFromUrl("gs://g-hero.appspot.com");
            pathReference = storageReference.child(listviewitem.getIconPath());
            View finalConvertView = convertView;
            pathReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Glide.with(finalConvertView.getContext())
                                .load(task.getResult())
                                .into(icon);
                        icon.setBackgroundResource(0);
                    }
                }
            });
            //icon.setImageResource(listviewitem.getIcon());
            content.setText(listviewitem.getContent());
            content2.setVisibility(View.INVISIBLE);
            date.setText(listviewitem.getDate());
            date2.setVisibility(View.INVISIBLE);
        }



        return convertView;
    }

}
