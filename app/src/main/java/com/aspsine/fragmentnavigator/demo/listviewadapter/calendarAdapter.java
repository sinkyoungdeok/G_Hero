package com.aspsine.fragmentnavigator.demo.listviewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.item.calendarListviewitem;

import java.util.ArrayList;

public class calendarAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<calendarListviewitem> data;
    private int layout;

    public calendarAdapter(Context context, int layout, ArrayList<calendarListviewitem> data) {
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
        if(view == null) {
            view = inflater.inflate(layout, viewGroup, false);
        }
        calendarListviewitem item = data.get(position);

        TextView tvStartTime= (TextView) view.findViewById(R.id.tv_start_time);
        TextView tvEndTime = (TextView) view.findViewById(R.id.tv_end_time);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_content);
        TextView tvNow = (TextView) view.findViewById(R.id.tv_now);
        View divier = (View) view.findViewById(R.id.divider);

        if(item.getEndTime().equals("") && item.getStartTime().equals("") && item.getContent().equals("")) {
            tvStartTime.setVisibility(View.INVISIBLE);
            tvEndTime.setVisibility(View.INVISIBLE);
            tvContent.setVisibility(View.INVISIBLE);
            tvNow.setVisibility(View.INVISIBLE);
            divier.setVisibility(View.INVISIBLE);
        } else {
            tvStartTime.setText(item.getStartTime());
            tvContent.setText(item.getContent());

            if (item.getEndTime() == null) {
                tvEndTime.setVisibility(View.INVISIBLE);
            } else {
                tvEndTime.setText(item.getEndTime());
            }
            tvNow.setVisibility(View.INVISIBLE); // 나중에 처리할 예정 ,, 시간이 지금시간하고 겹칠때 이 INVISIBLE하는 코드를 없애면 됨
        }
        return view;
    }
}
