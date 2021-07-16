package com.example.hintmachine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AnswerActivity extends AppCompatActivity {
    private ImageView hint_image;
    private TextView progress_text;
    private TextView code_text;
    private TextView answer_text;
    private String code;
    float itemCount;
    float progressCnt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        hint_image = (ImageView) findViewById(R.id.imageView);
        progress_text = (TextView) findViewById(R.id.progress_text);
        code_text = (TextView) findViewById(R.id.code_num_text);
        answer_text = (TextView) findViewById(R.id.answer_text);

        hint_image.setImageBitmap(StringToBitmap(PreferenceManager.getString(this, "Image")));
        hint_image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        String strContact = PreferenceManager.getString(this,code);
        Gson gson = new GsonBuilder().create();
        ItemData item = gson.fromJson(strContact, ItemData.class);

        answer_text.setText(item.getAnswer());
        if(PreferenceManager.getStringArrayPref(this,"codes") != null) {
            itemCount = (float)PreferenceManager.getStringArrayPref(this,"codes").size();
            progressCnt = (float)(PreferenceManager.getStringArrayPref(this,"codes").indexOf(code) + 1);
        }
        progress_text.setText(String.format("진행률 :%.2f%%",(progressCnt/itemCount)*100));
        code_text.setText("CODE 번호 : "+code);
    }

    public void onClickReturn(View view) {
        Intent intent = new Intent(this, HintActivity.class);
        intent.putExtra("code",code);
        startActivity(intent);
        finish();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HintActivity.class);
        intent.putExtra("code",code);
        startActivity(intent);
        finish();
    }
}