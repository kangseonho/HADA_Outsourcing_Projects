    package com.example.healthapp.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthapp.R;
import com.example.healthapp.ble.BlunoLibrary;
import com.example.healthapp.dto.PreferenceManager;
import com.example.healthapp.dto.SoundManager;

import java.util.List;

public class HealthPopupActivity extends AppCompatActivity {

    TextView title;
    Button goHealth;
    Button back_button;
    SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_popup);
        title = findViewById(R.id.health_popup_title);
        goHealth = findViewById(R.id.go_health);
        back_button = findViewById(R.id.back_button);
        soundManager = new SoundManager(this);

        Intent intent = getIntent();
        String data = intent.getStringExtra("health");
        title.setText(data);

        goHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playSound();
                Intent intent1 = new Intent(getApplicationContext(), HealthPopupGoActivity.class);
                startActivityForResult(intent1, 1);
                finish();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playSound();
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