package com.example.healthapp.screen;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.healthapp.Item.CalendarItem;
import com.example.healthapp.R;
import com.example.healthapp.adapter.CalendarAdapter;
import com.example.healthapp.dto.PreferenceManager;

public class CalendarActivity extends AppCompatActivity {

    final String[] healths = new String[] {"팔 운동","어깨 운동","다리 운동"};
    boolean healths_flag[] = {false, false, false};
    GridView calendarView;
    TextView monthText;
    CalendarAdapter adt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);

        adt = new CalendarAdapter(this,R.layout.layout_calendar_attribute); //어댑터 객체 생성
        calendarView = findViewById(R.id.calendarView); //그리드뷰 객체 참조
        calendarView.setAdapter(adt); //그리드뷰에 어댑터 설정

        calendarView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CalendarItem item = adt.getItem(position);
                if(item.getDay() != 0) {
                    new AlertDialog.Builder(CalendarActivity.this)
                            .setTitle("선택")
                            .setIcon(R.drawable.ic_launcher_foreground)
                            .setCancelable(false)
                            .setMultiChoiceItems(healths, healths_flag, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    healths_flag[which] = isChecked;
                                }
                            })
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        if(healths_flag[0]) {
                                            PreferenceManager.setInt(CalendarActivity.this,String.valueOf(adt.getCurYear())+String.valueOf(adt.getCurMonth())+String.valueOf(item.getDay())
                                            +"arm",View.VISIBLE);
                                        } else {
                                            PreferenceManager.setInt(CalendarActivity.this,String.valueOf(adt.getCurYear())+String.valueOf(adt.getCurMonth())+String.valueOf(item.getDay())
                                                    +"arm",View.INVISIBLE);
                                        }

                                        if(healths_flag[1]) {
                                            PreferenceManager.setInt(CalendarActivity.this,String.valueOf(adt.getCurYear())+String.valueOf(adt.getCurMonth())+String.valueOf(item.getDay())
                                                    +"shoulder",View.VISIBLE);
                                        } else {
                                            PreferenceManager.setInt(CalendarActivity.this,String.valueOf(adt.getCurYear())+String.valueOf(adt.getCurMonth())+String.valueOf(item.getDay())
                                                    +"shoulder",View.INVISIBLE);
                                        }

                                    if(healths_flag[2]) {
                                        PreferenceManager.setInt(CalendarActivity.this,String.valueOf(adt.getCurYear())+String.valueOf(adt.getCurMonth())+String.valueOf(item.getDay())
                                                +"leg",View.VISIBLE);
                                    } else {
                                        PreferenceManager.setInt(CalendarActivity.this,String.valueOf(adt.getCurYear())+String.valueOf(adt.getCurMonth())+String.valueOf(item.getDay())
                                                +"leg",View.INVISIBLE);
                                    }
                                    adt.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
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