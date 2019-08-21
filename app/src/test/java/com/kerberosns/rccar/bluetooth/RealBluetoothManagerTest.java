package com.kerberosns.rccar.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RealBluetoothManagerTest {
    private RealBluetoothManager mRealBluetoothManager;

    @Mock
    private Context context;

    @Mock
    private BluetoothAdapter bluetoothAdapter;


    @Before
    public void setUp() {
        mRealBluetoothManager = new RealBluetoothManager(context, bluetoothAdapter);
    }

    @Test
    public void isSupported() {
        // Verify.
        assertTrue(mRealBluetoothManager.isSupported());
    }

    @Test
    public void isNotSupported() {
        // Exercise.
        RealBluetoothManager mRealBluetoothManager = new RealBluetoothManager(context, null);

        // Verify.
        assertFalse(mRealBluetoothManager.isSupported());
    }
}
