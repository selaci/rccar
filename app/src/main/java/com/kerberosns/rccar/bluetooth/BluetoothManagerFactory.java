package com.kerberosns.rccar.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.kerberosns.rccar.Settings;

public class BluetoothManagerFactory {
    public static BluetoothManager newInstance(Settings settings, Context context) {
        if (settings.isDevelopmentMode()) {
            return new FakeBluetoothManager();
        } else {
            return new RealBluetoothManager(context, BluetoothAdapter.getDefaultAdapter());
        }
    }
}
