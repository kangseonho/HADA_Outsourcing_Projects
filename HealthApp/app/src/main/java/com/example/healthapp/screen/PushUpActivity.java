package com.example.healthapp.screen;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.healthapp.R;
import com.example.healthapp.ble.BluetoothSingleton;
import com.example.healthapp.dto.PreferenceManager;
import com.example.healthapp.dto.SoundManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PushUpActivity extends AppCompatActivity {

    TimerTask timerTask;
    Timer timer = new Timer();
    TextView target_set;
    TextView target_count;
    TextView current_set;
    TextView current_count;
    TextView rest_timer;
    TextView lazer_distance;
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
    Boolean flag = true;
    Boolean threadFlag = false;
    ConnectedThread connectedThread = null;
    Handler btHandler;
    SoundManager soundManager;

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
        soundManager = new SoundManager(this);

        BluetoothSingleton.getInstance().setBluetoothSocket();

        target_set = findViewById(R.id.target_set);
        target_count = findViewById(R.id.target_count);
        current_set = findViewById(R.id.current_set);
        current_count = findViewById(R.id.current_count);
        rest_timer = findViewById(R.id.rest_timer);
        lazer_distance = findViewById(R.id.lazer_distance_textView);
        progressBar_left = findViewById(R.id.progress_left);
        progressBar_right = findViewById(R.id.progress_right);
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

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rest_timer.setVisibility(View.VISIBLE);
                connectedThread = new ConnectedThread(BluetoothSingleton.getInstance().getBluetoothSocket());
                connectedThread.start();
                start_button.setVisibility(View.INVISIBLE);
                threadFlag = true;
            }
        });

        btHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 2) {
                    String readMessage = "";
                    String a = "";
                    String b = "";
                    String c = "";
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        a = readMessage.split(" ")[0];
                        b = readMessage.split(" ")[1];
                        c = readMessage.split(" ")[2];

                        progressBar_left.setProgress(Integer.valueOf(a));
                        progressBar_right.setProgress(Integer.valueOf(b));
                        lazer_distance.setText("지면과의 거리:"+ c);

                        if(Integer.valueOf(c) <= 25) {
                            if(flag) {
                                current_count_count++;
                                current_count.setText("현재 횟수: "+ current_count_count);
                                flag = false;

                                if(Math.abs(Integer.valueOf(a) - Integer.valueOf(b)) >= 20) {
                                    soundManager.playLazerSound();
                                } else if (Math.abs(Integer.valueOf(a) - Integer.valueOf(b)) < 20 && Math.abs(Integer.valueOf(a) - Integer.valueOf(b)) >= 10) {
                                    soundManager.playGoodSound();
                                } else {
                                    soundManager.playPerfectSound();
                                }
                            }
                        } else {
                            flag = true;
                        }

                        if (target_count_count <= current_count_count) {
                            current_count_count = 0;
                            current_count.setText("현재 횟수: "+ current_count_count+"회");
                            current_set_count++;
                            if(target_set_count <= current_set_count) {
                                connectedThread.cancel();
                                rest_timer.setText("운동 종료");
                            }
                            current_set.setText("현재 세트: " + current_set_count+"회");

                            progressBar_left.setProgress(0);
                            progressBar_right.setProgress(0);
                            startTimerTask();
                        }

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                if(threadFlag) {
                    try {
                        bytes = mmInStream.available();
                        if (bytes != 0) {
                            buffer = new byte[1024];
                            bytes = mmInStream.available();
                            bytes = mmInStream.read(buffer, 0, bytes);
                            btHandler.obtainMessage(2, bytes, -1, buffer).sendToTarget();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                        break;
                    }
                }
            }
        }

        public void write(int input) {
            try {
                mmOutStream.write(input);
            } catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private void startTimerTask()
    {
        stopTimerTask();
        threadFlag = false;
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
                            stopTimerTask();
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask,0 ,1000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        connectedThread.cancel();
    }

    private void stopTimerTask()
    {
        if(timerTask != null)
        {
            timerTask.cancel();
            timerTask = null;
            threadFlag = true;
        }
    }
}