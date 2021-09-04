package com.example.healthapp.dto;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthapp.R;


public class SoundManager {

    private Context mContext;
    private SoundPool soundPool;
    int soundID;

    public SoundManager(Context context) {
        mContext = context;
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundID = soundPool.load(mContext, R.raw.suctioncup,1);
    }

    public void playSound() {
        soundPool.play(soundID, 1F,1F,0,0,1F);
    }
}
