package com.kerberosns.rccar.bluetooth;

import java.io.IOException;

public abstract class BluetoothDeviceManager {
    private BluetoothManager mManager;
    private Device mDevice;
    boolean connected;

    public BluetoothDeviceManager(BluetoothManager manager, Device device) {
        mManager = manager;
        mDevice = device;
        connected = false;
    }

    public abstract boolean connect() throws IOException;
    public abstract boolean isConnected();
    public abstract boolean disconnect() throws IOException;
    public abstract void write(byte encoded) throws IOException;
}
