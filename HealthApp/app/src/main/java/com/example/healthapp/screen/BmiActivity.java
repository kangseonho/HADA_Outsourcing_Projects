package com.example.healthapp.screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.healthapp.R;

public class BmiActivity extends AppCompatActivity {

    private EditText height;
    private EditText weight;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        height = (EditText) findViewById(R.id.height);
        weight = (EditText) findViewById(R.id.weight);
        result = (TextView) findViewById(R.id.result);
    }

    public void onClickResult(View view) {
        if(!height.getText().toString().equals("") && !weight.getText().toString().equals("")) {
            String bmi = String.format("%.2f",caculateBMI(height.getText().toString(),weight.getText().toString()));
            String state = stateBMI(caculateBMI(height.getText().toString(),weight.getText().toString()));

            result.setText("BMI 수치: "+ bmi + " / "+"상태: "+ state);
        }
    }

    public void onClickBack(View view) {
        Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
        startActivity(intent);
    }

    private float caculateBMI(String height, String weight) {
        float mheight = Float.valueOf(height);
        float mWeight = Float.valueOf(weight);

        float resultValue = (float) (mWeight/ Math.pow((mheight/100),2));
        return resultValue;
    }

    private String stateBMI(float bmi) {
        String state = "";

        if (bmi < 20) {
            state = "저체중";
        } else if (bmi >= 20 && bmi <= 24) {
            state = "정상";
        }else if (bmi >= 25 && bmi <= 29 ) {
            state = "과체중";
        }else if (bmi >= 30) {
            state = "비만";
        }else {
            state = "옳지 않은 값";
        }

        return state;
    }
}