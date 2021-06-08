package com.example.hadagpstracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.SYSTEM_HEALTH_SERVICE;

public class MainFragment extends Fragment {

    View rootView;
    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    BluetoothSocket btSocket = null;
    ConnectedThread connectedThread = null;
    Handler btHandler;
    BluetoothAdapter btAdapter;
    Button btnParied;
    ListView bluetoothlistView;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;
    Geocoder geocoder;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    Button btnUp;
    LatLng origin;
    LatLng destination;
    ArrayList<MarkerItem> sampleList = new ArrayList();
    Marker circle;
    TimerHandler mTimer;
    TextView testView;
    int mTime;
    View marker_root_view;
    TextView tv_marker;
    double a = 37.26350;
    double b = 127.02780;
    int cnt = 1;
    int first=0;
    boolean chk = true;

    private final static int REQUEST_ENABLE_BT = 1;

    String[] permission_list = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };


    private final OnMapReadyCallback mapCallback = new OnMapReadyCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            if (circle != null) {
                circle.remove();
            }
            circle = googleMap.addMarker(new MarkerOptions()
                    .position(destination)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_a)));
            Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                    .add(
                            origin, destination
                    ).color(ContextCompat.getColor(getContext(), R.color.colorDraw)));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 15));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ActivityCompat.requestPermissions(getActivity(), permission_list, 1);
        bluetoothlistView = (ListView) rootView.findViewById(R.id.bluetoothlist);
        btnParied = (Button) rootView.findViewById(R.id.btn_paired);
        bluetoothlistView.setOnItemClickListener(new myBluetoothClickListener());
        btArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        deviceAddressArray = new ArrayList<>();
        bluetoothlistView.setAdapter(btArrayAdapter);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        testView = rootView.findViewById(R.id.testText);
        mTimer = new TimerHandler();
        geocoder = new Geocoder(getActivity());

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        getActivity().registerReceiver(receiver, filter);

        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        // 블루투스 데이터 수신 handler
        btHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 2) {
                    String readMessage = null;
                    try {
                        if(chk) {
                            StartTimer();
                            chk=false;
                        }
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        String[] pos = readMessage.split("/");
                        a = Double.parseDouble(pos[0]);
                        b = Double.parseDouble(pos[1]);
                        if(first == 0) {
                            origin = new LatLng(a,b);
                        }
                        destination = new LatLng(a,b);

                        mapFragment.getMapAsync(mapCallback);
                        first++;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //받아온 위도 경도
                }
            }
        };

        btnParied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButtonPaired();
            }
        });
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            //mapFragment.getMapAsync(mapCallback);
        }
    }

    public void onClickButtonPaired() {
        btArrayAdapter.clear();
        if (deviceAddressArray != null && !deviceAddressArray.isEmpty()) {
            deviceAddressArray.clear();
        }
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                System.out.println(device.getName());
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                System.out.println(device.getAddress());// MAC address
                btArrayAdapter.add(deviceName);
                deviceAddressArray.add(deviceHardwareAddress);
            }
        }
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        buffer = new byte[1024];
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        btHandler.obtainMessage(2, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        public void write(int input) {
            try {
                mmOutStream.write(input);
            } catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    public class myBluetoothClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            System.out.println(btArrayAdapter);
            final String name = btArrayAdapter.getItem(position);
            final String address = deviceAddressArray.get(position);
            boolean flag = true;

            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            try {
                btSocket = createBluetoothSocket(device);
                btSocket.connect();
            } catch (IOException e) {
                flag = false;
                e.printStackTrace();
            }

            if (flag) {
                connectedThread = new ConnectedThread(btSocket);
                connectedThread.start();
                System.out.println(name);
            }
            btArrayAdapter.clear();
        }

        private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
                return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
            } catch (Exception e) {
            }
            return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
        }
    }


    private void setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(getActivity()).inflate(R.layout.custommarker, null);
        tv_marker = (TextView) marker_root_view.findViewById(R.id.tv_marker);
    }

    private Marker addMarker(MarkerItem markerItem, boolean isSelectedMarker) {
        LatLng position = new LatLng(markerItem.getLat(), markerItem.getLon());
        int price = markerItem.getPrice();
        String formatted = Integer.toString(price);

        tv_marker.setText(formatted);
        tv_marker.setBackgroundResource(R.drawable.outlinebutton);
        tv_marker.setTextColor(Color.WHITE);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(Integer.toString(price));
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), marker_root_view)));
        return mMap.addMarker(markerOptions);
    }

    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    class TimerHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mTime++;
                    if (mTime >= 10) {
                        sampleList.add(new MarkerItem(a, b, cnt));
                        setCustomMarkerView();
                        for (MarkerItem markerItem : sampleList) {
                            addMarker(markerItem, false);
                        }
                        cnt++;
                        String temp = "";
                        try {
                            temp = geocoder.getFromLocation(a, b, 1).get(0).getAddressLine(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(cnt);
                        System.out.println(temp);
                        PreferenceManager.setInt(getActivity(),"Size",cnt-1);
                        PreferenceManager.setString(getActivity(),Integer.toString(cnt-1),(cnt-1)+")"+temp);
                        ((MapActivity)MapActivity.mContext).setListView();
                        sendEmptyMessage(1);
                    }
                    sendEmptyMessageDelayed(0, 1000);
                    break;
                case 1:
                    mTime = 0;
                    break;
                case 2:
                    removeMessages(0);
                    break;
            }
        }
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                btArrayAdapter.add(deviceName);
                deviceAddressArray.add(deviceHardwareAddress);
                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.sendEmptyMessage(2);
    }

    public void StartTimer() {
        mTimer.sendEmptyMessage(0);
    }
}
