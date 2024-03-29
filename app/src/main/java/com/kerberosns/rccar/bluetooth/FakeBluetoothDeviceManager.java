package com.kerberosns.rccar.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.kerberosns.rccar.MainActivity;

public class FakeBluetoothDeviceManager extends BluetoothDeviceManager {
    FakeBluetoothDeviceManager(BluetoothDevice bluetoothDevice) {
        super(bluetoothDevice);
    }

    @Override
    public boolean connect() {
        if (connected) {
            return false;
        } else {
            sleep();
            connected = true;
            return true;
        }
    }

    private void sleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO: Log message.
        }
    }
    public boolean isConnected() {
        return connected;
    }

    public boolean disconnect() {
        if (connected) {
            connected = false;
            return true;
        } else {
            return false;
        }
    }

    public void write(byte encoded) {
        Log.d(MainActivity.APPLICATION, "encoded: " + encoded);
    }
}
