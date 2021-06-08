package com.example.hadagpstracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {
    ArrayList<String> addressList;
    ListView listView;
    ArrayAdapter adapter;
    public static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mContext = this;
        addressList = new ArrayList<String>();
        listView = (ListView)findViewById(R.id.recordView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,addressList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.clear(mContext);
    }

    public void setListView() {
        int size = PreferenceManager.getInt(mContext,"Size");
        String record = PreferenceManager.getString(mContext, Integer.toString(size));
        addressList.add(record);
        adapter.notifyDataSetChanged();
    }
}