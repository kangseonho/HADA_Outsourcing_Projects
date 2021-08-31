package com.example.healthapp.screen;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.healthapp.R;

public class SelectHealthActivity extends AppCompatActivity {

    Button arm_health;
    Button leg_health;
    Button shoulder_health;
    Button back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_health);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);

        View.OnClickListener buttonListener =  new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.select_arm:
                        intent = new Intent(getApplicationContext(), HealthPopupActivity.class);
                        intent.putExtra("health", "팔 운동");
                        startActivityForResult(intent, 1);
                        break;
                    case R.id.select_leg:
                        intent = new Intent(getApplicationContext(), HealthPopupActivity.class);
                        intent.putExtra("health", "하체 운동");
                        startActivityForResult(intent, 1);
                        break;
                    case R.id.select_shoulder:
                        intent = new Intent(getApplicationContext(), HealthPopupActivity.class);
                        intent.putExtra("health", "어깨 운동");
                        startActivityForResult(intent, 1);
                        break;
                    case R.id.back_button:
                        intent = new Intent(getApplicationContext(), SelectActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        return;
                }
            }
        };

        findViewById(R.id.select_arm).setOnClickListener(buttonListener);
        findViewById(R.id.select_leg).setOnClickListener(buttonListener);
        findViewById(R.id.select_shoulder).setOnClickListener(buttonListener);
        findViewById(R.id.back_button).setOnClickListener(buttonListener);
    }
}