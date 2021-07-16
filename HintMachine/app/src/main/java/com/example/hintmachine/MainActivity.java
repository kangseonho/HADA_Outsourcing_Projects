package com.example.hintmachine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private EditText ptResult;
    private ImageView main_image;
    private TextView timer_text;
    private TextView hint_count_text;
    private TimerTask timerTask;
    private int startTime;
    private int progress;
    private Button button;
    private Timer timer = new Timer();
    private ArrayList<String> codeList = new ArrayList<>();
    ArrayList<String> temp = new ArrayList<>();
    private Button btn[] = new Button[16];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this,ForecdTerminationService.class));
        main_image = (ImageView)findViewById(R.id.imageView);
        timer_text = (TextView)findViewById(R.id.timer);
        hint_count_text = (TextView)findViewById(R.id.hint_count_text);
        ptResult = (EditText)findViewById(R.id.edit_text);
        button = (Button)findViewById(R.id.start_button);


        if(PreferenceManager.getInt(this,"curTime") != -1) {
            startTimerTask();
        } else {
            stopTimerTask();
        }

        if(PreferenceManager.getStringArrayPref(this,"codes") != null) {
            codeList = PreferenceManager.getStringArrayPref(this,"codes");
        }

        if(PreferenceManager.getStringArrayPref(this,"temp") != null) {
            temp = PreferenceManager.getStringArrayPref(this,"temp");
        }

        if(PreferenceManager.getInt(this,"progress") != -1) {
            progress = PreferenceManager.getInt(this,"progress");
        }
        hint_count_text.setText(Integer.toString(progress));

        System.out.println(PreferenceManager.getStringArrayPref(this,"temp"));
        main_image.setImageBitmap(StringToBitmap(PreferenceManager.getString(this,"Image")));
        main_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        initButton();
        initListener();
    }

    public void onClickEscape(View view) {
        if(ptResult.getText().toString().equals("*1230#")) {
            Intent intent = new Intent(getApplicationContext(),MachineManage.class);
            startActivity(intent);
            finish();
        }
    }

    public void onClickStart(View view) {
        PreferenceManager.setInt(this,"curTime",Long.valueOf(System.currentTimeMillis()).intValue());
        startTimerTask();
        view.setVisibility(View.INVISIBLE);
        ptResult.setVisibility(View.VISIBLE);
    }

    private void initButton() {
        btn[0] = findViewById(R.id.button_0);
        btn[1] = findViewById(R.id.button_1);
        btn[2] = findViewById(R.id.button_2);
        btn[3] = findViewById(R.id.button_3);
        btn[4] = findViewById(R.id.button_4);
        btn[5] = findViewById(R.id.button_5);
        btn[6] = findViewById(R.id.button_6);
        btn[7] = findViewById(R.id.button_7);
        btn[8] = findViewById(R.id.button_8);
        btn[9] = findViewById(R.id.button_9);
        btn[10] = findViewById(R.id.button_star);
        btn[11] = findViewById(R.id.button_shap);
        btn[12] = findViewById(R.id.button_clear);
        btn[13] = findViewById(R.id.button_back);
        btn[14] = findViewById(R.id.button_enter);
    }

    private void initListener(){
        for (int i=0; i<12;i++){
            btn[i].setOnClickListener((View view)-> {
                Button btn = (Button) view;
                ptResult.append(btn.getText().toString());
            });
        }
        btn[12].setOnClickListener((View view)-> {
            ptResult.setText("");
        });
        btn[13].setOnClickListener((View view)-> {
            String result = ptResult.getText().toString();
            if (!result.equals("")) {
                result = result.substring(0, result.length()-1);
                ptResult.setText(result);
            }
        });
        btn[14].setOnClickListener((View view)-> {
            String result = ptResult.getText().toString();
            if(!PreferenceManager.getString(this,result).equals("")) {
                if(codeList.contains(result)) {
                    temp.add(result);
                    PreferenceManager.setStringArrayPref(this,"temp",temp);
                    HashSet hs = new HashSet();
                    for(String item : temp) {
                        hs.add(item);
                    }
                    PreferenceManager.setInt(this,"progress",hs.size());
                }
                Intent intent = new Intent(this, HintActivity.class);
                intent.putExtra("code",result);
                startActivity(intent);
            } else if(result.equals("*0321#")) {
                SharedPreferences pref = getSharedPreferences("hintmachine", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("progress");
                editor.remove("temp");
                editor.remove("curTime");
                editor.commit();
                hint_count_text.setText("0");
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "CODE를 확인해 주세요",Toast.LENGTH_SHORT).show();
            }
            ptResult.setText("");
        });
    }

    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            button.setVisibility(View.INVISIBLE);
            ptResult.setVisibility(View.VISIBLE);
            if(PreferenceManager.getInt(getApplicationContext(),"Time") != -1) {
                startTime = PreferenceManager.getInt(getApplicationContext(),"Time");
            } else {
                startTime = 3600;
            }
            int cnt = startTime;
            cnt = cnt - ((Long.valueOf(System.currentTimeMillis()).intValue() - PreferenceManager.getInt(getApplicationContext(),"curTime"))/1000);
            int min = cnt / 60;
            int second = cnt % 60;
            String minformat="";
            String secondfomat="";
            if (min < 10) {
                minformat = String.format("0%1d",min);
            } else{
                minformat = String.format("%2d",min);
            }
            if (second < 10) {
                secondfomat = String.format("0%1d",second);
            } else {
                secondfomat = String.format("%2d",second);
            }
            timer_text.setText(minformat+":"+secondfomat);
            if(cnt <= 0) {
                stopTimerTask();
            }
            if(PreferenceManager.getInt(getApplicationContext(),"progress") != -1) {
                hint_count_text.setText(Integer.toString(PreferenceManager.getInt(getApplicationContext(),"progress")));
            }
        }
    };

    private void startTimerTask()
    {
        stopTimerTask();

        timerTask = new TimerTask()
        {
            int count = startTime;

            @Override
            public void run()
            {
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        };
        timer.schedule(timerTask,0 ,1000);
    }

    private void stopTimerTask()
    {
        if(timerTask != null)
        {
            timer_text.setText("00:00");
            timerTask.cancel();
            timerTask = null;
            SharedPreferences pref = getSharedPreferences("hintmachine", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("curTime");
            editor.commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
    }
}