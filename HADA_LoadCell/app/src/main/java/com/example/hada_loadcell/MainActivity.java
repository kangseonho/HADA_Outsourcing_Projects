package com.example.hada_loadcell;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    ProgressBar bar1;
    ProgressBar bar2;
    FirebaseDatabase database;
    DatabaseReference myRef_weight;

    //수액이 해당 값보다 작을 경우 푸시 알림 출력 ex) 10 = 100gram
    // 기준점을 바꾸고 싶을 경우 아래 point변수를 수정해주면 됨
    int point = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bar1 = findViewById(R.id.graph1);
        bar2 = findViewById(R.id.graph2);
        bar1.setProgress(100);
        bar1.setMax(100);

        bar2.setProgress(100);
        bar2.setMax(100);

        FirebaseApp.initializeApp(getApplicationContext());
        database = FirebaseDatabase.getInstance();
        myRef_weight = database.getReference("weight");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                if(key.equals("weight1")) {
                    Object obj = snapshot.getValue();
                    String weight1 = obj.toString();
                    float w1 = Float.parseFloat(weight1);
                    bar1.setProgress((int)(w1*100));

                    if(bar1.getProgress() <= point) {
                        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
                        NotificationCompat.Builder nb = notificationHelper.getChannelNotification("환자1의 수액이 얼마남지 않았습니다.");
                        notificationHelper.getManager().notify(1,nb.build());
                        Toast.makeText(getApplicationContext(),"환자1의 수액이 얼마남지 않았습니다.",Toast.LENGTH_LONG);
                    }
                }

                if(key.equals("weight2")) {
                    Object obj = snapshot.getValue();
                    String weight2 = obj.toString();
                    float w2 = Float.parseFloat(weight2);
                    bar2.setProgress((int)(w2*100));
                    if(bar2.getProgress() <= point) {
                        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
                        NotificationCompat.Builder nb = notificationHelper.getChannelNotification("환자2의 수액이 얼마남지 않았습니다.");
                        notificationHelper.getManager().notify(2,nb.build());
                        Toast.makeText(getApplicationContext(),"환자2의 수액이 얼마남지 않았습니다.",Toast.LENGTH_LONG);
                    }
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myRef_weight.addChildEventListener(childEventListener);
    }
}