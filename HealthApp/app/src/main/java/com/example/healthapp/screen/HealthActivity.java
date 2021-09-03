package com.example.healthapp.screen;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthapp.R;
import com.example.healthapp.ble.BlunoLibrary;
import com.example.healthapp.dto.PreferenceManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HealthActivity extends BlunoLibrary {

    TimerTask timerTask;
    Timer timer = new Timer();
    TextView target_set;
    TextView target_count;
    TextView current_set;
    TextView current_count;
    TextView rest_timer;
    ProgressBar progressBar_left;
    ProgressBar progressBar_right;
    Button stop_button;
    Button start_button;
    int target_set_count=0;
    int target_count_count=0;
    int current_set_count=0;
    int current_count_count=0;
    int rest_time=0;
    int progressBar_left_gage=0;
    int progressBar_right_gage=0;
    int init_right=0;
    int init_left=0;
    boolean flag_left = true;
    boolean flag_right = true;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

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

        target_set = findViewById(R.id.target_set);
        target_count = findViewById(R.id.target_count);
        current_set = findViewById(R.id.current_set);
        current_count = findViewById(R.id.current_count);
        rest_timer = findViewById(R.id.rest_timer);
        progressBar_left = findViewById(R.id.progress_left);
        progressBar_right = findViewById(R.id.progress_right);
        stop_button = findViewById(R.id.stop_button);
        start_button = findViewById(R.id.start_button);

        target_set_count = PreferenceManager.getInt(getApplicationContext(),"set_count");
        target_count_count = PreferenceManager.getInt(getApplicationContext(), "each_count");
        rest_time = PreferenceManager.getInt(getApplicationContext(), "rest_time");
        init_left = PreferenceManager.getInt(getApplicationContext(), "initValueLeft");
        init_right = PreferenceManager.getInt(getApplicationContext(), "initValueRight");

        target_set.setText("목표 세트: " + target_set_count+"회");
        target_count.setText("목표 횟수 " + target_count_count+"회");
        current_count.setText("현재 세트: " + current_count_count+"회");
        current_set.setText("현재 횟수 " + current_set_count+"회");
        progressBar_left.setProgress(progressBar_left_gage);
        progressBar_right.setProgress(progressBar_right_gage);
        rest_timer.setText("운동 중...");

        request(1000, new OnPermissionsResult() {
            @Override
            public void OnSuccess() {
                Toast.makeText(HealthActivity.this,"성공",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFail(List<String> noPermissions) {
                Toast.makeText(HealthActivity.this,"실패",Toast.LENGTH_SHORT).show();
            }
        });

        onCreateProcess();
        serialBegin(115200);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonScanOnClickProcess();
            }
        });

        stop_button.setOnClickListener(new View.OnClickListener() {
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

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();														//onPause Process by BlunoLibrary
    }

    protected void onResume(){
        super.onResume();
        System.out.println("BlUNOActivity onResume");
        onResumeProcess();														//onResume Process by BlunoLibrary
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
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        switch (theConnectionState) {											//Four connection state
            case isConnected:
                Toast.makeText(HealthActivity.this,"Connected",Toast.LENGTH_SHORT).show();
                serialSend("1");
                break;
            case isConnecting:
                Toast.makeText(HealthActivity.this,"Connecting",Toast.LENGTH_SHORT).show();
                break;
            case isToScan:
                Toast.makeText(HealthActivity.this,"Scan",Toast.LENGTH_SHORT).show();
                break;
            case isScanning:
                Toast.makeText(HealthActivity.this,"Scanning",Toast.LENGTH_SHORT).show();
                break;
            case isDisconnecting:
                Toast.makeText(HealthActivity.this,"Disconnectiong",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSerialReceived(String theString) {
        String[] array = theString.split(",");
        int left = Integer.valueOf(array[0]);
        int right = Integer.valueOf(array[1]);

        left = left - init_left;
        right = right - init_right;


        if( 0 < left && left <= 40) {
            flag_left = true;
        }

        if( 0 < right && right <= 40) {
            flag_right = true;
        }

        if(flag_left && flag_right) {
            flag = true;
        }

        progressBar_left.setProgress(left);
        progressBar_right.setProgress(right);

        if(flag) {
            if (flag_left && left > 80) {
                current_count_count++;
                current_count.setText("현재 횟수: " + current_count_count + "회");
                flag_left = false;
            }
            else if (flag_right && right > 80 ) {
                current_count_count++;
                current_count.setText("현재 횟수: " + current_count_count+"회");
                flag_right = false;
            }
            flag = false;
        }

        if (target_count_count <= current_count_count) {
            current_count_count = 0;
            current_count.setText("현재 횟수: "+ current_count_count+"회");
            current_set_count++;
            if(target_set_count <= current_set_count) {
                onStop();
                onDestroyProcess();
                rest_timer.setText("운동 종료");
            }

            current_set.setText("현재 세트: " + current_set_count+"회");

            progressBar_left.setProgress(0);
            progressBar_right.setProgress(0);
            serialSend("2");

            startTimerTask();
        }
    }

    private void startTimerTask()
    {
        stopTimerTask();

        timerTask = new TimerTask()
        {
            int count = rest_time;

            @Override
            public void run()
            {
                count--;
                rest_timer.post(new Runnable() {
                    @Override
                    public void run() {
                        rest_timer.setText("휴식 시간: " + count + " 초");
                    }
                });

                if(count <= 0) {
                    rest_timer.post(new Runnable() {
                        @Override
                        public void run() {
                            rest_timer.setText("운동 중...");
                            serialSend("1");
                            stopTimerTask();
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask,0 ,1000);
    }

    private void stopTimerTask()
    {
        if(timerTask != null)
        {
            timerTask.cancel();
            timerTask = null;
        }
    }
}