package com.kerberosns.rccar.bluetooth;

import android.content.Context;

import com.kerberosns.rccar.Settings;

public class BluetoothDeviceManagerFactory {
    public static BluetoothDeviceManager newInstance(Context context, Settings settings, Device device) {
        BluetoothManager bluetoothManager = BluetoothManagerFactory.newInstance(settings, context);

        if (settings.isDevelopmentMode()) {
            return new FakeBluetoothDeviceManager(bluetoothManager, device);
        } else {
            return new RealBluetoothDeviceManager(bluetoothManager, device);
        }
    }
}
