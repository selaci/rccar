package com.kerberosns.rccar.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.io.IOException;

public abstract class BluetoothDeviceManager {
    private BluetoothManager mManager;
    private BluetoothDevice mBluetoothDevice;
    boolean connected;

    public BluetoothDeviceManager(BluetoothDevice bluetoothDevice) {
        mBluetoothDevice = bluetoothDevice;
        connected = false;
    }

    public abstract boolean connect() throws IOException;
    public abstract boolean isConnected();
    public abstract boolean disconnect() throws IOException;
    public abstract void write(byte encoded) throws IOException;
}
