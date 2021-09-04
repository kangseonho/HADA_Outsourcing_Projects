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

public class SelectHealthSystemActivity extends AppCompatActivity {

    Button system_button1;
    Button system_button2;
    Button back_button;
    SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_health_system);

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

        system_button1 = findViewById(R.id.system1);
        system_button2 = findViewById(R.id.system2);
        back_button = findViewById(R.id.back_button);

        system_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playSound();
                Intent intent = new Intent(getApplicationContext(), HealthActivity.class);
                startActivity(intent);
            }
        });

        system_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playSound();
                Intent intent = new Intent(getApplicationContext(), HealthActivity.class);
                startActivity(intent);
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playSound();
                Intent intent = new Intent(getApplicationContext(), SelectHealthActivity.class);
                startActivity(intent);
            }
        });
    }
}