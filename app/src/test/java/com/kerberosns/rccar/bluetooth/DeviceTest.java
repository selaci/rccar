package com.kerberosns.rccar.bluetooth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DeviceTest {
    private Device mDevice;

    @Before
    public void setUp() {
        mDevice = new Device("Bluetooth Device Name", "00:0a:95:9d:68:16", true);
    }

    @Test
    public void getName() {
        assertEquals(mDevice.getName(), "Bluetooth Device Name");
    }

    @Test
    public void getAddress() {
        assertEquals(mDevice.getAddress(), "00:0a:95:9d:68:16");
    }

    @Test
    public void isPaired() {
        assertTrue(mDevice.isPaired());
    }
}
