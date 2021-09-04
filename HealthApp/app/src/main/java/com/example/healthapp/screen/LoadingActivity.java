package com.example.healthapp.screen;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.healthapp.R;
import com.example.healthapp.dto.SoundManager;

public class LoadingActivity extends AppCompatActivity {
    SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        soundManager = new SoundManager(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void onTouchToScreen(View view) {
        soundManager.playSound();
        Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
        startActivity(intent);
    }
}