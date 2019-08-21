package com.kerberosns.rccar.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.ArraySet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Android has a bluetooth adapter that publishes a lot of behaviour. This class wraps around this
 * adapter and publishes a reduced set, just what this apps needs. It supports the headset profile.
 *
 * It also hides implementation to make things easier for the class that calls this.
 */
public class RealBluetoothManager extends BluetoothManager {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHeadset mBluetoothHeadset;
    private Context mContext;

    RealBluetoothManager(Context context, BluetoothAdapter bluetoothAdapter) {
        mContext = context;
        mBluetoothAdapter = bluetoothAdapter;
    }

    @Override
    public boolean isSupported() {
        return mBluetoothAdapter != null;
    }

    @Override
    public boolean isEnabled() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    @Override
    public boolean openProfile() {
        if (mBluetoothAdapter == null) {
            return false;
        }

        if (mBluetoothHeadset == null) {
            BluetoothProfile.ServiceListener listener = new BluetoothProfile.ServiceListener() {
                @Override
                public void onServiceConnected(int profile,
                                               BluetoothProfile proxy) {

                    if (profile == BluetoothProfile.HEADSET) {
                        mBluetoothHeadset = (BluetoothHeadset) proxy;
                    }
                }

                @Override
                public void onServiceDisconnected(int profile) {
                    if (profile == BluetoothProfile.HEADSET) {
                        mBluetoothHeadset = null;
                    }
                }
            };

            return mBluetoothAdapter.getProfileProxy(mContext.getApplicationContext(),
                    listener, BluetoothProfile.HEADSET);
        }

        return false;
    }

    @Override
    public boolean closeProfile() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
            return true;
        }

        return false;
    }

    @Override
    public boolean startDiscovery() {
        return mBluetoothAdapter != null && mBluetoothAdapter.startDiscovery();
    }

    @Override
    public boolean cancelDiscovery() {
        return mBluetoothAdapter != null && mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    public boolean isDiscovering() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.isDiscovering();
        } else {
            return false;
        }
    }

    @Override
    public List<Device> getDevices() {
        // TODO: Investigate if this should return all devices, not only the paired ones.
        if (mBluetoothAdapter != null) {
            return assembleDeviceList(mBluetoothAdapter.getBondedDevices());
        } else {
            return new ArrayList<>();
        }
    }

    private List<Device> assembleDeviceList(Set<BluetoothDevice> devices) {
        List<Device> assembledList = new ArrayList<>();
        for(BluetoothDevice device : devices) {
            assembledList.add(
                    new Device(
                        device.getName(),
                        device.getAddress(),
                    device.getBondState() == BluetoothDevice.BOND_BONDED));
        }

        return assembledList;
    }

    @Override
    public void createBond(Device device) throws Exception {
        if (Build.VERSION.SDK_INT > 19) {
            BluetoothDevice bluetoothDevice = getBluetoothDevice(device);
            if (bluetoothDevice != null) {
                bluetoothDevice.createBond();
            } else {
                throw new Exception("Bluetooth device is null during a create bond operation");
            }
        } else {
            // TODO: This needs to be tested.
            Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
            Method createBondMethod = class1.getMethod("createBond");
            createBondMethod.invoke(device);
        }
    }

    @Override
    public BluetoothDevice getBluetoothDevice(Device device) {
        for(BluetoothDevice bluetoothDevice: mBluetoothAdapter.getBondedDevices()) {
            if (device.getName().equals(bluetoothDevice.getName())) {
                return bluetoothDevice;
            }
        }

        return null;
    }
}
