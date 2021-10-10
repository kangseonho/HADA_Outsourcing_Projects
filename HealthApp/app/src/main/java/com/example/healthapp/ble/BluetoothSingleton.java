package com.example.healthapp.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothSingleton {
    private static BluetoothSingleton instance;
    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket btSocket;
    private BluetoothDevice device;

    private BluetoothSingleton() {}

    public static synchronized BluetoothSingleton getInstance() {
        if (instance == null ) {
            instance = new BluetoothSingleton();
        }
        return instance;
    }

    public void setBluetoothSocket() {
        try {
            btSocket = createBluetoothSocket(device);
            btSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBluetoothDevice(BluetoothDevice _device) {
        device = _device;
    }

    public BluetoothSocket getBluetoothSocket() {
        return btSocket;
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
