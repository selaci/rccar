package com.kerberosns.rccar.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import static android.bluetooth.BluetoothDevice.EXTRA_DEVICE;

/**
 * This class implements a broadcast receiver for bluetooth-related messages. It expects to process
 * device found, device name changed and device bond state changed. The main focus is to list all
 * bluetooth devices that have been found by the phone in one way or the other.
 *
 * There are other messages that may help detect bluetooth devices, however I've observed that only
 * the messages this class filters in help detect bluetooth devices that are paired or ready to be
 * paired. Other messages may detect more bluetooth devices, but for some reason they never paired
 * or event went off the list of devices.
 */
public class BluetoothBroadcastReceiver extends BroadcastReceiver {
    public interface ActionListener {
        void onBluetoothDeviceFound(BluetoothDevice device);
        void onBluetoothDeviceChanged(BluetoothDevice device);
    }

    private IntentFilter filter;
    private List<ActionListener> actionListeners;

    public BluetoothBroadcastReceiver() {
        filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        actionListeners = new ArrayList<>();
    }

    @VisibleForTesting
    BluetoothBroadcastReceiver(IntentFilter intentFilter) {
        filter = intentFilter;
        actionListeners = new ArrayList<>();
    }

    public void addActionListener(ActionListener actionListener) {
        actionListeners.add(actionListener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
        if (action != null) {
            switch (action) {
                case BluetoothDevice.ACTION_FOUND: {
                    if (device != null && device.getName() != null) {
                        callListenersWhenDeviceFound(device);
                    }
                    break;
                }
                case BluetoothDevice.ACTION_NAME_CHANGED: {
                    callListenersWhenDeviceChanged(device);
                    break;
                }
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    callListenersWhenDeviceChanged(device);
                    break;
            }
        }
    }

    private void callListenersWhenDeviceFound(BluetoothDevice device) {
        for (ActionListener actionListener : actionListeners) {
            actionListener.onBluetoothDeviceFound(device);
        }
    }

    private void callListenersWhenDeviceChanged(BluetoothDevice device) {
        for (ActionListener actionListener : actionListeners) {
            actionListener.onBluetoothDeviceChanged(device);
        }
    }

    public void register(FragmentActivity activity) {
        activity.registerReceiver(this, filter);
    }

    public void unregister(FragmentActivity activity) {
        activity.unregisterReceiver(this);
    }
}
