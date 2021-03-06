package com.example.healthapp.screen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.healthapp.R;
import com.example.healthapp.ble.BluetoothSingleton;
import com.example.healthapp.dto.SoundManager;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class SelectActivity extends AppCompatActivity {

    SoundManager soundManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
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
        View.OnClickListener buttonListener =  new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.select_health:
                        intent = new Intent(getApplicationContext(), SelectHealthActivity.class);
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
                        intent = new Intent(getApplicationContext(), Bluetooth_popup.class);
                        startActivityForResult(intent, 1);
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

//    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
//        try {
//            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
//            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
//        } catch (Exception e) {
//        }
//        return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode==RESULT_OK) {
                BluetoothDevice bluetoothDevice = data.getExtras().getParcelable("bluetooth");
                BluetoothSingleton.getInstance().setBluetoothDevice(bluetoothDevice);
            }
        }
    }
}