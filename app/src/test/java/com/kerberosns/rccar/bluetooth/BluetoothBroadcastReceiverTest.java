package com.kerberosns.rccar.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.appcompat.app.AppCompatActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static android.bluetooth.BluetoothDevice.EXTRA_DEVICE;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BluetoothBroadcastReceiverTest {
    private BluetoothBroadcastReceiver mReceiver;

    @Mock
    IntentFilter intentFilter;

    @Mock
    Context context;

    @Mock
    Intent intent;

    @Mock
    BluetoothDevice device;

    @Mock
    BluetoothBroadcastReceiver.ActionListener listener;

    @Mock
    AppCompatActivity activity;

    @Before
    public void setUp() {
        mReceiver = new BluetoothBroadcastReceiver(intentFilter);
    }


    /**
     * Verify the broadcast receiver reacts to messages of type ACTION_FOUND.
     */
    @Test
    public void onReceiveActionFound() {
        // Mock.
        when(device.getName()).thenReturn("foo");
        when(intent.getAction()).thenReturn(BluetoothDevice.ACTION_FOUND);
        when(intent.getParcelableExtra(EXTRA_DEVICE)).thenReturn(device);
        doNothing().when(listener).onBluetoothDeviceFound(device);

        // Exercise.
        mReceiver.addActionListener(listener);
        mReceiver.onReceive(context, intent);

        // Verify.
        verify(listener, times(1)).onBluetoothDeviceFound(device);
    }

    /**
     * Verify the broadcast receiver reacts to messages of type ACTION_FOUND, but does not call
     * any listener when the devices don't have a name.
     */
    @Test
    public void onReceiveActionFoundButDeviceHasNoName() {
        // Mock.
        when(device.getName()).thenReturn(null);
        when(intent.getAction()).thenReturn(BluetoothDevice.ACTION_FOUND);
        when(intent.getParcelableExtra(EXTRA_DEVICE)).thenReturn(device);

        // Exercise.
        mReceiver.addActionListener(listener);
        mReceiver.onReceive(context, intent);

        // Verify.
        verify(listener, times(0)).onBluetoothDeviceFound(device);
    }

    /**
     * Verify the broadcast receiver reacts to messages of type ACTION_NAME_CHANGED.
     */
    @Test
    public void onReceiveActionNameChanged() {
        // Mock.
        when(intent.getAction()).thenReturn(BluetoothDevice.ACTION_NAME_CHANGED);
        when(intent.getParcelableExtra(EXTRA_DEVICE)).thenReturn(device);
        doNothing().when(listener).onBluetoothDeviceFound(device);

        // Exercise.
        mReceiver.addActionListener(listener);
        mReceiver.onReceive(context, intent);

        // Verify.
        verify(listener, times(1)).onBluetoothDeviceFound(device);
    }

    /**
     * Verify the broadcast receiver reacts to messages of type ACTION_BOND_STATE_CHANGED.
     */
    @Test
    public void onReceiveActionBondStateChanged() {
        // Mock.
        when(intent.getAction()).thenReturn(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        when(intent.getParcelableExtra(EXTRA_DEVICE)).thenReturn(device);
        doNothing().when(listener).onBluetoothDeviceFound(device);

        // Exercise.
        mReceiver.addActionListener(listener);
        mReceiver.onReceive(context, intent);

        // Verify.
        verify(listener, times(1)).onBluetoothDeviceFound(device);
    }


    /**
     * Verify activity registers.
     */
    @Test
    public void registerActivity() {
        // Mock.
        when(activity.registerReceiver(mReceiver, intentFilter)).thenReturn(intent);

        // Exercise.
        mReceiver.register(activity);

        // Verify.
        verify(activity, times(1)).registerReceiver(mReceiver, intentFilter);
    }

    /**
     * Verify activity unregisters.
     */
    @Test
    public void deregisterActivity() {
        // Mock.
        doNothing().when(activity).unregisterReceiver(mReceiver);

        // Exercise.
        mReceiver.unregister(activity);

        // Verify.
        verify(activity, times(1)).unregisterReceiver(mReceiver);
    }
}
