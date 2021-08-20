package com.example.healthapp.screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.healthapp.R;
import com.example.healthapp.adapter.CalendarAdapter;

public class CalendarActivity extends AppCompatActivity {

    GridView calendarView;
    TextView monthText;
    CalendarAdapter adt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        adt = new CalendarAdapter(this,R.layout.layout_calendar_attribute); //어댑터 객체 생성
        calendarView = findViewById(R.id.calendarView); //그리드뷰 객체 참조
        calendarView.setAdapter(adt); //그리드뷰에 어댑터 설정

        calendarView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
            }
        });

        monthText = findViewById(R.id.monthText);
        setMonthText();

        Button monthPrevious = findViewById(R.id.monthPrevious);
        Button monthNext = findViewById(R.id.monthNext);

        // 뒤로가기 버튼 이벤트 리스너 설정
        monthPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adt.setPreviousMonth();
                adt.notifyDataSetChanged(); //어댑터 데이터 갱신하고 뷰 다시 뿌리기
                setMonthText();
            }
        });

        // 앞으로 가기 버튼에 이벤트 리스너 설정
        monthNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adt.setNextMonth();
                adt.notifyDataSetChanged(); //어댑터 데이터 갱신하고 뷰 다시 뿌리기
                setMonthText();
            }
        });
    }

    public void setMonthText(){
        int curYear = adt.getCurYear();
        int curMonth = adt.getCurMonth();
        monthText.setText(curYear+"년 "+(curMonth+1)+"월");
    }
}