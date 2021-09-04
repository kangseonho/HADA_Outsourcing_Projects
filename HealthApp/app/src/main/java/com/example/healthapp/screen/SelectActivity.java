package com.example.healthapp.screen;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.healthapp.R;
import com.example.healthapp.dto.SoundManager;

public class SelectActivity extends AppCompatActivity {

    Button select_health;
    Button select_bmi;
    Button select_calendar;
    Button select_setting;
    SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        soundManager = new SoundManager(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        View view = getLayoutInflater().inflate(R.layout.custom_bar,
                null);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layoutParams);
        Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        View.OnClickListener buttonListener =  new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.select_health:
                        soundManager.playSound();
                        intent = new Intent(getApplicationContext(), SelectHealthActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.select_bmi:
                        soundManager.playSound();
                        intent = new Intent(getApplicationContext(),BmiActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.select_calendar:
                        soundManager.playSound();
                        intent = new Intent(getApplicationContext(),CalendarActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.select_setting:
                        soundManager.playSound();
                        break;
                    default:
                        return;
                }
            }
        };

        findViewById(R.id.select_health).setOnClickListener(buttonListener);
        findViewById(R.id.select_bmi).setOnClickListener(buttonListener);
        findViewById(R.id.select_calendar).setOnClickListener(buttonListener);
        findViewById(R.id.select_setting).setOnClickListener(buttonListener);
    }
}