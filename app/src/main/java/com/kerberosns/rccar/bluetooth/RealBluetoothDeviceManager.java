package com.kerberosns.rccar.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class RealBluetoothDeviceManager extends BluetoothDeviceManager {
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;

    RealBluetoothDeviceManager(BluetoothManager manager, Device device) {
        super(manager, device);
        mBluetoothDevice = manager.getBluetoothDevice(device);
    }

    public boolean connect() throws IOException {
        UUID uuid = mBluetoothDevice.getUuids()[0].getUuid();

        mSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
        mSocket.connect();
        connected = true;

        mOutputStream = mSocket.getOutputStream();
        return connected;
    }

    public boolean isConnected() {
        if (mSocket != null) {
            return mSocket.isConnected();
        } else {
            return false;
        }
    }

    public boolean disconnect() throws IOException{
        mSocket.close();
        connected = false;

        return false;
    }

    public void write(byte encoded) throws IOException {
        mOutputStream.write(encoded);
    }
}
