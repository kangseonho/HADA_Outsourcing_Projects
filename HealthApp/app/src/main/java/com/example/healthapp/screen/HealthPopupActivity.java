    package com.example.healthapp.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthapp.R;
import com.example.healthapp.ble.BlunoLibrary;
import com.example.healthapp.dto.PreferenceManager;

import java.util.List;

public class HealthPopupActivity extends BlunoLibrary {

    TextView title;
    Button init;
    Button goHealth;
    Button back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_popup);
        title = findViewById(R.id.health_popup_title);
        init = findViewById(R.id.init_setting);
        goHealth = findViewById(R.id.go_health);
        back_button = findViewById(R.id.back_button);

        Intent intent = getIntent();
        String data = intent.getStringExtra("health");
        title.setText(data);

        request(1000, new OnPermissionsResult() {
            @Override
            public void OnSuccess() {
                Toast.makeText(HealthPopupActivity.this,"성공",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFail(List<String> noPermissions) {
                Toast.makeText(HealthPopupActivity.this,"실패",Toast.LENGTH_SHORT).show();
            }
        });

        onCreateProcess();
        serialBegin(115200);

        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonScanOnClickProcess();
            }
        });

        goHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), HealthPopupGoActivity.class);
                startActivityForResult(intent1, 1);
                finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }


    protected void onResume(){
        super.onResume();
        System.out.println("BlUNOActivity onResume");
        onResumeProcess();														//onResume Process by BlunoLibrary
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            onPauseProcess();
        } catch (IllegalArgumentException e) {

        }
        														//onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();														//onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
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

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionStateEnum) {
        switch (theConnectionStateEnum) {											//Four connection state
            case isConnected:
                Toast.makeText(HealthPopupActivity.this,"Connected",Toast.LENGTH_SHORT).show();
                serialSend("0");
                System.out.println("adfasdfasdfasdfsfsf");
                break;
            case isConnecting:
                Toast.makeText(HealthPopupActivity.this,"Connecting",Toast.LENGTH_SHORT).show();
                break;
            case isToScan:
                Toast.makeText(HealthPopupActivity.this,"Scan",Toast.LENGTH_SHORT).show();
                break;
            case isScanning:
                Toast.makeText(HealthPopupActivity.this,"Scanning",Toast.LENGTH_SHORT).show();
                break;
            case isDisconnecting:
                Toast.makeText(HealthPopupActivity.this,"Disconnectiong",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSerialReceived(String theString) {

        String[] array = theString.split(",");

        Toast.makeText(HealthPopupActivity.this,"초기 값: "+theString+"으로 설정 완료.",Toast.LENGTH_SHORT).show();
        PreferenceManager.setInt(getApplicationContext(),"initValueLeft",Integer.parseInt(array[0]));
        PreferenceManager.setInt(getApplicationContext(),"initValueRight",Integer.parseInt(array[1]));
    }
}