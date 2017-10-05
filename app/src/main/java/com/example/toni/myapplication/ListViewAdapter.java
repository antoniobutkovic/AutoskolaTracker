package com.example.toni.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Toni on 27.9.2017..
 */

public class ListViewAdapter extends BaseAdapter{

    private ArrayList<String> studentInfo = new ArrayList<String>();
    private ArrayList<String> rideNum = new ArrayList<String>();
    private Context context;

    public ListViewAdapter() {

    }

    public ListViewAdapter(Context context, ArrayList<String> studentInfo, ArrayList<String> rideNum) {
        this.studentInfo = studentInfo;
        this.context = context;
        this.rideNum = rideNum;
    }



    @Override
    public int getCount() {
        return studentInfo.size();
    }

    @Override
    public Object getItem(int i) {
        return studentInfo.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.custom_listview, null);
        }
        TextView studentInfoTv = (TextView) v.findViewById(R.id.studentInfoTv);
        TextView rideNumTv = (TextView) v.findViewById(R.id.rideNumTv);
        studentInfoTv.setText(studentInfo.get(i));
        rideNumTv.setText(rideNum.get(i));
        return v;
    }
}
