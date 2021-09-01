package com.example.healthapp.screen;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.healthapp.R;
import com.example.healthapp.dto.PreferenceManager;


public class HealthPopupGoActivity extends Activity {

    Button ok_button;
    Button back_button;
    EditText set_count;
    EditText each_count;
    EditText rest_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_popup_go);

        ok_button = findViewById(R.id.ok_button);
        back_button = findViewById(R.id.back_button);
        set_count = findViewById(R.id.set_count);
        each_count = findViewById(R.id.each_count);
        rest_time = findViewById(R.id.rest_time);

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(set_count.getText().toString().equals("") || each_count.getText().toString().equals("") || rest_time.getText().toString().equals("")) {
                    Toast.makeText(HealthPopupGoActivity.this,"잘못된 입력입니다.",Toast.LENGTH_SHORT).show();
                } else {
                    PreferenceManager.setInt(getApplicationContext(),"set_count",Integer.valueOf(set_count.getText().toString()));
                    PreferenceManager.setInt(getApplicationContext(),"each_count",Integer.valueOf(each_count.getText().toString()));
                    PreferenceManager.setInt(getApplicationContext(),"rest_time",Integer.valueOf(rest_time.getText().toString()));

                    Intent intent = new Intent(getApplicationContext(),SelectHealthSystemActivity.class);
                    startActivity(intent);
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}