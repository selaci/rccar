package com.kerberosns.rccar.bluetooth;

public class FakeBluetoothDeviceManager extends BluetoothDeviceManager {
    public FakeBluetoothDeviceManager(BluetoothManager manager, Device device) {
        super(manager, device);
    }

    @Override
    public boolean connect() {
        if (connected) {
            return false;
        } else {
            sleep();
            connected = true;
            return true;
        }
    }

    private void sleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO: Log message.
        }
    }
    public boolean isConnected() {
        return connected;
    }

    public boolean disconnect() {
        if (connected) {
            connected = false;
            return true;
        } else {
            return false;
        }
    }

    public void write(byte encoded) {}
}
