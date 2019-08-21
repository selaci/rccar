package com.kerberosns.rccar.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;

public class FakeBluetoothManager extends BluetoothManager {
    private boolean discovering;

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean openProfile() {
        return true;
    }

    @Override
    public boolean closeProfile() {
        return true;
    }

    @Override
    public boolean startDiscovery() {
        discovering = true;
        return true;
    }

    @Override
    public boolean cancelDiscovery() {
        discovering = false;
        return true;
    }

    @Override
    public boolean isDiscovering() {
        return discovering;
    }

    @Override
    public List<Device> getDevices() {

        List<Device> devices = new ArrayList<>();

        devices.add(new Device("HC-05", "00:0A:95:9D:68:16", true));
        devices.add(new Device("Living Room","00:14:22:01:23:45", true));
        devices.add(new Device("Phone","00:A0:C9:14:C8:29", true));

        return devices;
    }

    @Override
    public void createBond(Device device) {}

    @Override
    public BluetoothDevice getBluetoothDevice(Device device) {
        throw new RuntimeException("This is not implemented in the \"FakeBluetoothManager\".");
    }
}
