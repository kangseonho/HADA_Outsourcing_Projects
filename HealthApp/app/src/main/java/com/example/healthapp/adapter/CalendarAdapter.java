package com.example.healthapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthapp.Item.CalendarItem;
import com.example.healthapp.R;
import com.example.healthapp.dto.PreferenceManager;
import com.example.healthapp.screen.CalendarActivity;

import java.util.Calendar;

public class CalendarAdapter extends BaseAdapter {
    Calendar cal;

    Context mContext;

    CalendarItem[] items;

    LayoutInflater inf;
    int curYear;
    int layout;
    int curMonth;

    public CalendarAdapter(Context context, int layout){
        super();
        mContext = context;
        this.layout = layout;
        init();
    }

    public void init(){
        inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cal = Calendar.getInstance(); //Calendar 객체 가져오기
        items = new CalendarItem[7*6]; //아이템 크기 결정

        calculate();//날짜 계산해서 items[] 배열 값 설정
    }

    public void calculate(){
        for(int i=0; i<items.length; i++){ //items[] 모든 값 0으로 초기화
            items[i] = new CalendarItem(0);
        }

        cal.set(Calendar.DAY_OF_MONTH, 1); //1일로 설정

        int startDay = cal.get(Calendar.DAY_OF_WEEK); //현재 달 1일의 요일 (1: 일요일, . . . 7: 토요일)
        int lastDay = cal.getActualMaximum(Calendar.DATE); //달의 마지막 날짜

        int cnt = 1;
        for(int i=startDay-1; i<startDay-1+lastDay; i++){ /* 1일의 요일에 따라 시작위치 다르고 마지막 날짜까지 값 지정*/
            items[i] = new CalendarItem(cnt);
            cnt++;
        }

        curYear = cal.get(Calendar.YEAR);
        curMonth = cal.get(Calendar.MONTH);
    }

    public void setPreviousMonth(){ //한 달 앞으로 가고 다시 계산
        cal.add(Calendar.MONTH, -1);
        calculate();
    }

    public void setNextMonth(){
        cal.add(Calendar.MONTH, 1); //한 달 뒤로가고 다시 계산
        calculate();
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inf.inflate(layout,null);
            convertView.setBackgroundColor(Color.WHITE);
        }

        TextView tv_date = (TextView)convertView.findViewById(R.id.date);
        TextView tv_arm = (TextView)convertView.findViewById(R.id.arm);
        TextView tv_shoulder = (TextView)convertView.findViewById(R.id.shoulder);
        TextView tv_leg = (TextView)convertView.findViewById(R.id.leg);

        CalendarItem item = items[position];

        if(item.getDay() != 0) {
            tv_date.setText(String.valueOf(item.getDay()));
        } else {
            tv_date.setText("");
            item.setArm(View.INVISIBLE);
            item.setShoulder(View.INVISIBLE);
            item.setLeg(View.INVISIBLE);
        }

        if(PreferenceManager.getInt(mContext,String.valueOf(getCurYear())+String.valueOf(getCurMonth())+String.valueOf(item.getDay())+"arm") != -1) {
            item.setArm(PreferenceManager.getInt(mContext,String.valueOf(getCurYear())+String.valueOf(getCurMonth())+String.valueOf(item.getDay()+"arm")));
            tv_arm.setVisibility(item.isArm());
        } else {
            item.setArm(View.INVISIBLE);
            tv_arm.setVisibility(item.isArm());
        }

        if(PreferenceManager.getInt(mContext,String.valueOf(getCurYear())+String.valueOf(getCurMonth())+String.valueOf(item.getDay())+"shoulder") != -1) {
            item.setShoulder(PreferenceManager.getInt(mContext,String.valueOf(getCurYear())+String.valueOf(getCurMonth())+String.valueOf(item.getDay())+"shoulder"));
            tv_shoulder.setVisibility(item.isShoulder());
        } else {
            item.setShoulder(View.INVISIBLE);
            tv_shoulder.setVisibility(item.isShoulder());
        }

        if(PreferenceManager.getInt(mContext,String.valueOf(getCurYear())+String.valueOf(getCurMonth())+String.valueOf(item.getDay())+"leg") != -1) {
            item.setLeg(PreferenceManager.getInt(mContext,String.valueOf(getCurYear())+String.valueOf(getCurMonth())+String.valueOf(item.getDay())+"leg"));
            tv_leg.setVisibility(item.isLeg());
        } else {
            item.setLeg(View.INVISIBLE);
            tv_leg.setVisibility(item.isLeg());
        }



        if(position%7==0){
            tv_date.setTextColor(Color.RED);
        }

        return convertView;
    }


    @Override
    public CalendarItem getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public int getCurYear(){
        return curYear;
    }

    public int getCurMonth(){
        return curMonth;
    }
}
