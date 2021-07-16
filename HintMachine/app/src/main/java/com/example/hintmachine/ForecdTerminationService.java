package com.example.hintmachine;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class ForecdTerminationService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onTaskRemoved(Intent rootIntent) {
            SharedPreferences pref = getSharedPreferences("hintmachine", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("curTime");
            editor.commit();
            stopSelf();
        }
}

