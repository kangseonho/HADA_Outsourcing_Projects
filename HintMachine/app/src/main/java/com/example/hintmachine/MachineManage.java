package com.example.hintmachine;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MachineManage extends AppCompatActivity {

    private EditText codeText;
    private EditText hintText;
    private EditText answerText;
    private EditText timeText;
    private Context context;
    private ListView listView;
    private Button delete_button;
    private ImageView iv;
    private ArrayAdapter<String> itemAdapter;
    private ArrayList<String> codes = new ArrayList<>();
    private ArrayList<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_manage);
        context = this;
        codeText = (EditText)findViewById(R.id.input_code);
        hintText = (EditText)findViewById(R.id.input_hint);
        answerText = (EditText)findViewById(R.id.input_answer);
        timeText = (EditText)findViewById(R.id.input_time);
        listView = (ListView)findViewById(R.id.itemlistView);
        delete_button = (Button)findViewById(R.id.code_delete);
        iv = (ImageView)findViewById(R.id.select_imageView);

        if(PreferenceManager.getString(context,"Image") != null) {
            iv.setImageBitmap(StringToBitmap(PreferenceManager.getString(context,"Image")));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        if(PreferenceManager.getInt(context,"Time") != -1) {
            timeText.setText(Integer.toString(PreferenceManager.getInt(context,"Time")));
        }

        if(PreferenceManager.getStringArrayPref(this,"codes") != null) {
            codes = PreferenceManager.getStringArrayPref(this,"codes");
        }

        if(PreferenceManager.getStringArrayPref(this,"items") != null) {
            items = PreferenceManager.getStringArrayPref(this,"items");
        }



        itemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,items);
        listView.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
                int count = itemAdapter.getCount() ;

                for (int i = count-1; i >= 0; i--) {
                    if (checkedItems.get(i)) {
                        SharedPreferences pref = getSharedPreferences("hintmachine", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.remove(codes.get(i));
                        editor.commit();
                        System.out.println(i);
                        items.remove(i);
                        codes.remove(i);
                        PreferenceManager.setStringArrayPref(getApplicationContext(),"items",items);
                        PreferenceManager.setStringArrayPref(getApplicationContext(),"codes",codes);
                    }
                }

                listView.clearChoices() ;
                itemAdapter.notifyDataSetChanged();
            }
        });
    }

    public void onClickItemSave(View view) {
        if(codes.contains(codeText.getText().toString())) {
            Toast.makeText(getApplicationContext(),"중복된 코드입니다.",Toast.LENGTH_SHORT).show();
        } else {
            ItemData item = new ItemData();
            item.setCode(codeText.getText().toString());
            item.setHint(hintText.getText().toString());
            item.setAnswer(answerText.getText().toString());

            codes.add(codeText.getText().toString());
            PreferenceManager.setStringArrayPref(this,"codes",codes);

            Gson gson = new GsonBuilder().create();
            String strContact = gson.toJson(item, ItemData.class);
            PreferenceManager.setString(context,codeText.getText().toString(),strContact);
            items.add(strContact);
            PreferenceManager.setStringArrayPref(this,"items",items);
            itemAdapter.notifyDataSetChanged();
        }
    }

    public void onClickTimeSave(View view) {
        if(!timeText.getText().toString().equals("")) {
            PreferenceManager.setInt(context,"Time",Integer.parseInt(timeText.getText().toString()));
        }
    }

    public void onClickSelectImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*") ;
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1001);
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

    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1001){
            if(resultCode == RESULT_OK) {
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    PreferenceManager.setString(context,"Image",BitmapToString(img));
                    iv.setImageBitmap(StringToBitmap(PreferenceManager.getString(context,"Image")));
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }
}