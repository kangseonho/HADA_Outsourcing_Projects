package com.example.healthapp.ble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthapp.R;

import java.util.ArrayList;
import java.util.List;

public abstract  class BlunoLibrary extends AppCompatActivity {

    private Context mainContext=this;
    private  String [] mStrPermission = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private List<String>  mPerList   = new ArrayList<>();
    private List<String>  mPerNoList = new ArrayList<>();

    private  OnPermissionsResult permissionsResult;
    private  int requestCode;

    public abstract void onConectionStateChange(connectionStateEnum theconnectionStateEnum);
    public abstract void onSerialReceived(String theString);
    public void serialSend(String theString){
        if (mConnectionState == connectionStateEnum.isConnected) {
            mSCharacteristic.setValue(theString);
            mBluetoothLeService.writeCharacteristic(mSCharacteristic);
        }
    }

    private int mBaudrate=115200;	//set the default baud rate to 115200
    private String mPassword="AT+PASSWOR=DFRobot\r\n";


    private String mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";

//	byte[] mBaudrateBuffer={0x32,0x00,(byte) (mBaudrate & 0xFF),(byte) ((mBaudrate>>8) & 0xFF),(byte) ((mBaudrate>>16) & 0xFF),0x00};;


    public void serialBegin(int baud){
        mBaudrate=baud;
        mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";
    }


    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
    private static BluetoothGattCharacteristic mSCharacteristic, mModelNumberCharacteristic, mSerialPortCharacteristic, mCommandCharacteristic;
    BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluno;
    private boolean mScanning =false;
    AlertDialog mScanDeviceDialog;
    private String mDeviceName;
    private String mDeviceAddress;
    public enum connectionStateEnum{isNull, isScanning, isToScan, isConnecting , isConnected, isDisconnecting};
    public connectionStateEnum mConnectionState = connectionStateEnum.isNull;
    private static final int REQUEST_ENABLE_BT = 1;

    private Handler mHandler= new Handler();

    public boolean mConnected = false;

    private final static String TAG = BlunoLibrary.class.getSimpleName();

    private Runnable mConnectingOverTimeRunnable=new Runnable(){

        @Override
        public void run() {
            if(mConnectionState==connectionStateEnum.isConnecting)
                mConnectionState=connectionStateEnum.isToScan;
            onConectionStateChange(mConnectionState);
            mBluetoothLeService.close();
        }};

    private Runnable mDisonnectingOverTimeRunnable=new Runnable(){

        @Override
        public void run() {
            if(mConnectionState==connectionStateEnum.isDisconnecting)
                mConnectionState=connectionStateEnum.isToScan;
            onConectionStateChange(mConnectionState);
            mBluetoothLeService.close();
        }};

    public static final String SerialPortUUID="0000dfb1-0000-1000-8000-00805f9b34fb";
    public static final String CommandUUID="0000dfb2-0000-1000-8000-00805f9b34fb";
    public static final String ModelNumberStringUUID="00002a24-0000-1000-8000-00805f9b34fb";

    public void onCreateProcess()
    {
        if(!initiate()){
            Toast.makeText(mainContext, "bluetooth not supported",Toast.LENGTH_SHORT).show();
            ((Activity) mainContext).finish();
        }


        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void onConnection() {
        final BluetoothDevice device = mBluno;
        if (device == null)
            return;
        scanLeDevice(false);

        if(device.getName()==null || device.getAddress()==null)
        {
            mConnectionState=connectionStateEnum.isToScan;
            onConectionStateChange(mConnectionState);
        }
        else{

            System.out.println("onListItemClick " + device.getName().toString());

            System.out.println("Device Name:"+device.getName() + "   " + "Device Name:" + device.getAddress());

            mDeviceName=device.getName();
            mDeviceAddress=device.getAddress();
            if (mBluetoothLeService.connect(mDeviceAddress)) {
                Log.d(TAG, "Connect request success");
                mConnectionState=connectionStateEnum.isConnecting;
                onConectionStateChange(mConnectionState);
                mHandler.postDelayed(mConnectingOverTimeRunnable, 1000);
            }
            else {
                Log.d(TAG, "Connect request fail");
                mConnectionState=connectionStateEnum.isToScan;
                onConectionStateChange(mConnectionState);
            }
        }
    }



    public void onResumeProcess() {
        System.out.println("BlUNOActivity onResume");
        // Ensures Bluetooth is enabled on the device. If Bluetooth is not
        // currently enabled,
        // fire an intent to display a dialog asking the user to grant
        // permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) mainContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }


        mainContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

    }


    public void onPauseProcess() {
        System.out.println("BLUNOActivity onPause");
        scanLeDevice(false);

        mainContext.unregisterReceiver(mGattUpdateReceiver);
        mConnectionState= connectionStateEnum.isToScan;
        onConectionStateChange(mConnectionState);
    //    mScanDeviceDialog.dismiss();

        if(mBluetoothLeService!=null)
        {
            mBluetoothLeService.disconnect();
            mHandler.postDelayed(mDisonnectingOverTimeRunnable, 10000);

//			mBluetoothLeService.close();
        }
        mSCharacteristic=null;

    }


    public void onStopProcess() {
        System.out.println("MiUnoActivity onStop");
        if(mBluetoothLeService!=null)
        {
			mBluetoothLeService.disconnect();
            mHandler.removeCallbacks(mDisonnectingOverTimeRunnable);
            mBluetoothLeService.close();
        }
        mSCharacteristic=null;
    }

    public void onDestroyProcess() {
        mainContext.unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    public void onActivityResultProcess(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            ((Activity) mainContext).finish();
            return;
        }
    }

    boolean initiate()
    {
        // Use this check to determine whether BLE is supported on the device.
        // Then you can
        // selectively disable BLE-related features.
        if (!mainContext.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) mainContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            return false;
        }
        return true;
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @SuppressLint("DefaultLocale")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            System.out.println("mGattUpdateReceiver->onReceive->action="+action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                mHandler.removeCallbacks(mConnectingOverTimeRunnable);

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                mConnectionState = connectionStateEnum.isToScan;
                onConectionStateChange(mConnectionState);
                mHandler.removeCallbacks(mDisonnectingOverTimeRunnable);
                mBluetoothLeService.close();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                for (BluetoothGattService gattService : mBluetoothLeService.getSupportedGattServices()) {
                    System.out.println("ACTION_GATT_SERVICES_DISCOVERED  "+
                            gattService.getUuid().toString());
                }
                getGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                if(mSCharacteristic==mModelNumberCharacteristic)
                {
                    if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toUpperCase().startsWith("DF BLUNO")) {
                        mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, false);
                        mSCharacteristic=mCommandCharacteristic;
                        mSCharacteristic.setValue(mPassword);
                        mBluetoothLeService.writeCharacteristic(mSCharacteristic);
                        mSCharacteristic.setValue(mBaudrateBuffer);
                        mBluetoothLeService.writeCharacteristic(mSCharacteristic);
                        mSCharacteristic=mSerialPortCharacteristic;
                        mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
                        mConnectionState = connectionStateEnum.isConnected;
                        onConectionStateChange(mConnectionState);

                    }
                    else {
                        Toast.makeText(mainContext, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
                        mConnectionState = connectionStateEnum.isToScan;
                        onConectionStateChange(mConnectionState);
                    }
                }
                else if (mSCharacteristic==mSerialPortCharacteristic) {
                    onSerialReceived(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                }


                System.out.println("displayData "+intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    protected void buttonScanOnClickProcess()
    {
        switch (mConnectionState) {
            case isNull:
                mConnectionState=connectionStateEnum.isScanning;
                onConectionStateChange(mConnectionState);
                scanLeDevice(true);
                break;
            case isToScan:
                mConnectionState=connectionStateEnum.isScanning;
                onConectionStateChange(mConnectionState);
                scanLeDevice(true);
                break;
            case isScanning:
                break;

            case isConnecting:

                break;
            case isConnected:
                mBluetoothLeService.disconnect();
                mHandler.postDelayed(mDisonnectingOverTimeRunnable, 10000);

//			mBluetoothLeService.close();
                mConnectionState=connectionStateEnum.isDisconnecting;
                onConectionStateChange(mConnectionState);
                break;
            case isDisconnecting:

                break;

            default:
                break;
        }


    }

    void scanLeDevice(final boolean enable) {
        if (enable) {
            if(!mScanning)
            {
                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            if(mScanning)
            {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

    // Code to manage Service lifecycle.
    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            System.out.println("mServiceConnection onServiceConnected");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                ((Activity) mainContext).finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            System.out.println("mServiceConnection onServiceDisconnected");
            mBluetoothLeService = null;
        }
    };

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            ((Activity) mainContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(device.getAddress().equals("D0:B5:C2:8F:77:5F")) {
                        mBluno = device;
                        onConnection();
                    }
                }
            });
        }
    };

    private void getGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        mModelNumberCharacteristic=null;
        mSerialPortCharacteristic=null;
        mCommandCharacteristic=null;
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            System.out.println("displayGattServices + uuid="+uuid);

            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                uuid = gattCharacteristic.getUuid().toString();
                if(uuid.equals(ModelNumberStringUUID)){
                    mModelNumberCharacteristic=gattCharacteristic;
                    System.out.println("mModelNumberCharacteristic  "+mModelNumberCharacteristic.getUuid().toString());
                }
                else if(uuid.equals(SerialPortUUID)){
                    mSerialPortCharacteristic = gattCharacteristic;
                    System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
                }
                else if(uuid.equals(CommandUUID)){
                    mCommandCharacteristic = gattCharacteristic;
                    System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
                }
            }
            mGattCharacteristics.add(charas);
        }

        if (mModelNumberCharacteristic==null || mSerialPortCharacteristic==null || mCommandCharacteristic==null) {
            Toast.makeText(mainContext, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
            mConnectionState = connectionStateEnum.isToScan;
            onConectionStateChange(mConnectionState);
        }
        else {
            mSCharacteristic=mModelNumberCharacteristic;
            mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
            mBluetoothLeService.readCharacteristic(mSCharacteristic);
        }

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void request(int requestCode, OnPermissionsResult permissionsResult){
        if(!checkPermissionsAll()){
            requestPermissionAll(requestCode, permissionsResult);
        }
    }

    protected boolean checkPermissions(String permissions){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int check = checkSelfPermission(permissions);
            return check == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    protected boolean checkPermissionsAll(){
        mPerList.clear();
        for(int i = 0; i < mStrPermission.length; i++ ){
            boolean check = checkPermissions(mStrPermission[i]);
            if(!check){
                mPerList.add(mStrPermission[i]);
            }
        }
        return mPerList.size() > 0 ? false : true;
    }

    protected void requestPermission(String[] mPermissions, int requestCode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(mPermissions,requestCode);
        }
    }

    protected void requestPermissionAll(int requestCode, OnPermissionsResult permissionsResult){
        this.permissionsResult = permissionsResult;
        requestPermission((String[]) mPerList.toArray(new String[mPerList.size()]),requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == requestCode){
            if(grantResults.length>0){
                for(int i = 0; i < grantResults.length; i++){
                    if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                        System.out.println(permissions[i]);
                        mPerNoList.add(permissions[i]);
                    }
                }
                if(permissionsResult != null){
                    if(mPerNoList.size() == 0){
                        permissionsResult.OnSuccess();
                    }else {
                        permissionsResult.OnFail(mPerNoList);
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public interface OnPermissionsResult{
        void OnSuccess();
        void OnFail(List<String> noPermissions);
    }
}
