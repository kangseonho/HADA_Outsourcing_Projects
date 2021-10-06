package com.example.healthapp.screen;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Notification;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.healthapp.R;
import com.example.healthapp.dto.SoundManager;

public class BmiActivity extends AppCompatActivity {

    private EditText height;
    private EditText weight;
    private TextView result;
    private Button result_button;
    private Button back_button;
    SoundManager soundManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

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


        height = (EditText) findViewById(R.id.height);
        weight = (EditText) findViewById(R.id.weight);
        result = (TextView) findViewById(R.id.result);
        result_button = (Button) findViewById(R.id.result_button);
        back_button = (Button) findViewById(R.id.back_button);

        result_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playSound();
                if(!height.getText().toString().equals("") && !weight.getText().toString().equals("")) {
                    String bmi = String.format("%.2f",caculateBMI(height.getText().toString(),weight.getText().toString()));
                    String state = stateBMI(caculateBMI(height.getText().toString(),weight.getText().toString()));

                    result.setText("BMI 수치: "+ bmi + " / "+"상태: "+ state);
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playSound();
                Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
                startActivity(intent);
            }
        });
    }


    private float caculateBMI(String height, String weight) {
        float mheight = Float.valueOf(height);
        float mWeight = Float.valueOf(weight);

        float resultValue = (float) (mWeight/ Math.pow((mheight/100),2));
        return resultValue;
    }

    private String stateBMI(float bmi) {
        String state = "";
        System.out.println(bmi);
        if (bmi < 20) {
            state = "저체중";
        } else if (bmi >= 20 && bmi <= 25) {
            state = "정상";
        }else if (bmi >= 25 && bmi <= 30 ) {
            state = "과체중";
        }else if (bmi > 30) {
            state = "비만";
        }else {
            state = "옳지 않은 값";
        }
        return state;
    }
}