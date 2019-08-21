package com.kerberosns.rccar.rccar;

import android.util.Log;

import com.kerberosns.rccar.MainActivity;
import com.kerberosns.rccar.bluetooth.BluetoothDeviceManager;

import java.io.IOException;

public class Driver {
    public static final int MAX_THRUST_FORWARD = 5;
    public static final int MAX_THRUST_BACKWARD = -5;

    public static final int MAX_STEERING_LEFT = -5;
    public static final int MAX_STEERING_RIGHT = 5;

    private boolean mWasMovingForward;
    private boolean mWasMovingBackward;

    private BluetoothDeviceManager mDeviceManager;

    private byte lastCommand;

    public Driver(BluetoothDeviceManager deviceManager) {
        mDeviceManager = deviceManager;
    }

    public void rectifyThrust(int thrust) {
        if (thrust >= MAX_THRUST_FORWARD / 2) {
            moveForward();
        } else if (thrust <= MAX_THRUST_BACKWARD / 2) {
            moveBackward();
        } else {
            stop();
        }
    }

    public void stopThrust() {
        stop();
    }

    public void rectifySteering(int steering) {
        if (steering <= MAX_STEERING_LEFT / 2) {
            turnLeft();
        } else if (steering >= MAX_STEERING_RIGHT / 2) {
            turnRight();
        } else {
            centerSteering();
        }
    }

    public void centerSteering() {
        if (wasMovingForward()) {
            moveForward();
        } else if (wasMovingBackward()) {
            moveBackward();
        } else {
            stop();
        }
    }

    private void moveForward() {
        mWasMovingForward = true;
        mWasMovingBackward = false;

        write((byte) 0x01);
    }

    private void moveBackward() {
        mWasMovingForward = false;
        mWasMovingBackward = true;

        write((byte) 0x02);
    }

    private void write(byte command) {
        if (lastCommand != command) {
            lastCommand = command;

            try {
                mDeviceManager.write(command);
            } catch (IOException e) {
                // TODO: Should I toast / log the exception?
            }
        }
    }

    private void stop() {
        mWasMovingForward = false;
        mWasMovingBackward = false;

        write((byte) 0x00);
    }

    private void turnLeft() {
        write((byte) 0x03);
    }

    private void turnRight() {
        write((byte) 0x04);
    }

    private boolean wasMovingForward() {
        return mWasMovingForward;
    }

    private boolean wasMovingBackward() {
        return mWasMovingBackward;
    }
}
