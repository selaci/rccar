package com.kerberosns.rccar.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.kerberosns.rccar.Settings;

public class BluetoothDeviceManagerFactory {
    public static BluetoothDeviceManager newInstance(Settings settings, BluetoothDevice bluetoothDevice) {
        if (settings.isDevelopmentMode()) {
            return new FakeBluetoothDeviceManager(bluetoothDevice);
        } else {
            return new RealBluetoothDeviceManager(bluetoothDevice);
        }
    }
}
