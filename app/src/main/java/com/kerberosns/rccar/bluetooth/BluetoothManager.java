package com.kerberosns.rccar.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * With this class I intend to do two things:
 *   1. Offer an abstraction in order to use bluetooth in the real devices and the emulated ones.
 *      Android does not support bluetooth in the emulated devices, so I want to create a fake
 *      bluetooth manager when using emulation. At the same time, I need a common interface when
 *      using a real device. This is the point number one of this class.
 *
 *   2. Android has a bluetooth adapter that publishes a lot of behaviour. This class wraps around
 *      this adapter and publishes a reduced set, just what this apps needs. It supports the headset
 *      profile.
 *
 *      It also hides implementation to make things easier for the class that calls this.
 */
public abstract class BluetoothManager {
    public abstract boolean isSupported();
    public abstract boolean isEnabled();
    public abstract boolean openProfile();
    public abstract boolean closeProfile();
    public abstract boolean startDiscovery();
    public abstract boolean cancelDiscovery();
    public abstract boolean isDiscovering();
    public abstract List<Device> getDevices();
    public abstract void createBond(Device device) throws  Exception;
    public abstract BluetoothDevice getBluetoothDevice(Device device);
    public abstract void addBluetoothDevice(BluetoothDevice bluetoothDevice);
}