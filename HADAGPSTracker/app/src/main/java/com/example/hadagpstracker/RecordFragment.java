package com.example.hadagpstracker;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class RecordFragment extends Fragment {
    ArrayList<String> addressList;
    ListView listView;
    Bundle bundle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_record, container, false);
        bundle = new Bundle();
        addressList = new ArrayList<String>();
        listView = (ListView) rootView.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,addressList);
        return rootView;
    }

    public void setListView(String item) {
        addressList.add(item);
        ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

}