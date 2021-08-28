package com.example.healthapp.screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.healthapp.R;

public class SelectActivity extends AppCompatActivity {

    Button select_health;
    Button select_bmi;
    Button select_calendar;
    Button select_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        View.OnClickListener buttonListener =  new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.select_health:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.select_bmi:
                        intent = new Intent(getApplicationContext(),BmiActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.select_calendar:
                        intent = new Intent(getApplicationContext(),CalendarActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.select_setting:
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