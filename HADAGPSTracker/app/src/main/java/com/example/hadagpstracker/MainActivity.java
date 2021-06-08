package com.example.hadagpstracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View.OnClickListener onMenuClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.tab_Map:
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.tab_login:
                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nid.naver.com/nidlogin.login?mode=qrcode"));
                        startActivity(intent2);
                        break;
                    default:
                        return;
                }
            }
        };
        findViewById(R.id.tab_Map).setOnClickListener(onMenuClick);
        findViewById(R.id.tab_login).setOnClickListener(onMenuClick);
    }
}