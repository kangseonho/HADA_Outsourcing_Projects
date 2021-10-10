package com.example.healthapp.dto;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthapp.R;


public class SoundManager {

    private Context mContext;
    private SoundPool soundPool;
    int flankID;
    int pushupID;
    int squrtID;
    int lazerID;
    int goodID;
    int perfectID;


    public SoundManager(Context context) {
        mContext = context;
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        flankID = soundPool.load(mContext, R.raw.flank, 1);
        pushupID = soundPool.load(mContext, R.raw.pushup, 1);
        squrtID = soundPool.load(mContext, R.raw.squrt, 1);
        lazerID = soundPool.load(mContext, R.raw.lazer, 5);
        goodID = soundPool.load(mContext, R.raw.good, 4);
        perfectID = soundPool.load(mContext, R.raw.perfect, 4);
    }

    public void playFlankSound() {
        soundPool.play(flankID, 1F,1F,0,0,1F);
    }
    public void playPushUpSound() {
        soundPool.play(pushupID, 1F,1F,0,0,1F);
    }
    public void playSqurtSound() {
        soundPool.play(squrtID, 1F,1F,0,0,1F);
    }
    public void playLazerSound() {
        soundPool.play(lazerID, 1F,1F,0,0,1F);
    }
    public void playGoodSound() {
        soundPool.play(goodID, 1F,1F,0,0,1F);
    }
    public void playPerfectSound() {
        soundPool.play(perfectID, 1F,1F,0,0,1F);
    }
}
