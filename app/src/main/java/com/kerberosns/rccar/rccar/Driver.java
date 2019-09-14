package com.kerberosns.rccar.rccar;

import com.kerberosns.rccar.bluetooth.BluetoothDeviceManager;

import java.io.IOException;

public class Driver {
    public static final int MAX_THRUST_FORWARD  = 5;
    public static final int MAX_THRUST_BACKWARD = -5;

    public static final int MAX_STEERING_LEFT  = -5;
    public static final int MAX_STEERING_RIGHT = 5;

    private boolean mWasMovingForward;
    private boolean mWasMovingBackward;
    private boolean mSteering;

    private BluetoothDeviceManager mDeviceManager;

    private byte lastCommand;

    // Commands.
    private static final byte STOP          = 0x00;
    private static final byte MOVE_FORWARD  = 0x01;
    private static final byte MOVE_BACKWARD = 0x02;
    private static final byte TURN_LEFT     = 0x03;
    private static final byte TURN_RIGHT    = 0x04;
    private static final byte NEXT_SEQUENCE = 0x05;
    private static final byte LEFT_SPEED    = 0x06;
    private static final byte RIGHT_SPEED   = 0x07;

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
        mSteering = false;
        if (wasMovingForward()) {
            moveForward();
        } else if (wasMovingBackward()) {
            moveBackward();
        } else {
            stop();
        }
    }

    public void nextSequence() {
        send(NEXT_SEQUENCE);
    }

    private void moveForward() {
        mWasMovingForward = true;
        mWasMovingBackward = false;

        if (!isSteering()) {
            sendMovement(MOVE_FORWARD);
        }
    }

    private void moveBackward() {
        mWasMovingForward = false;
        mWasMovingBackward = true;

        if (!isSteering()) {
            sendMovement(MOVE_BACKWARD);
        }
    }

    private void sendMovement(byte command) {
        if (lastCommand != command) {
            lastCommand = command;

            send(command);
        }
    }

    private void send(byte command) {
        try {
            mDeviceManager.write(command);
        } catch (IOException e) {
            // TODO: Should I toast / log the exception?
        }
    }

    private void stop() {
        mWasMovingForward = false;
        mWasMovingBackward = false;

        if (!isSteering()) {
            sendMovement(STOP);
        }
    }

    private void turnLeft() {
        mSteering = true;
        sendMovement(TURN_LEFT);
    }

    private void turnRight() {
        mSteering = true;
        sendMovement(TURN_RIGHT);
    }

    private boolean wasMovingForward() {
        return mWasMovingForward;
    }

    private boolean wasMovingBackward() {
        return mWasMovingBackward;
    }

    private boolean isSteering() {
        return mSteering;
    }

    public void setLeftSpeed(int value) {
        byte command = (byte) (((value << 4) & 0xF0) + LEFT_SPEED);
        send(command);
    }

    public void setRightSpeed(int value) {
        byte command = (byte) (((value << 4) & 0xF0) + RIGHT_SPEED);
        send(command);
    }
}
