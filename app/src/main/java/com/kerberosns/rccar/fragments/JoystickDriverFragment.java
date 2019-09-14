package com.kerberosns.rccar.fragments;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kerberosns.rccar.MainActivity;
import com.kerberosns.rccar.R;
import com.kerberosns.rccar.Settings;
import com.kerberosns.rccar.rccar.Driver;

public class JoystickDriverFragment extends Fragment {
    private Context mContext;
    private Driver mDriver;

    private ImageView lInner, lCentre;
    private ImageView rInner, rCentre;

    static Fragment newInstance(Settings settings) {
        Bundle args = new Bundle();
        args.putParcelable(MainActivity.SETTINGS, settings);

        Fragment fragment = new JoystickDriverFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle args = getArguments();
        Settings settings = null;
        BluetoothDevice bluetoothDevice = null;

        if (args != null) {
            settings = args.getParcelable(MainActivity.SETTINGS);
        }

        if (settings == null) {
            toastMessage(R.string.settings_not_found);
        }

        if (bluetoothDevice == null) {
            toastMessage(R.string.device_not_found);
        }

        mDriver = new Driver(JoystickFragment.mDeviceManager);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.joystick_drive, container, false);

        setViewsForLeftJoystick(view);
        setViewsForRightJoystick(view);

        Button led = view.findViewById(R.id.led);
        led.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate();
                mDriver.nextSequence();
            }
        });

        return view;
    }

    private float yInc, yComp;
    private float yInner, yCentre;
    private float y0;

    @SuppressLint("ClickableViewAccessibility")
    private void setViewsForLeftJoystick(View view) {
        final ImageView lOuter = view.findViewById(R.id.lOuter);
        lInner = view.findViewById(R.id.lInner);
        lCentre = view.findViewById(R.id.lCentre);

        // Set pixel positions.
        lCentre.post(new Runnable() {
            @Override
            public void run() {
                yInner = lInner.getY();
                yCentre = lCentre.getY();

                yInc = lCentre.getY() - lOuter.getY();
                yComp = (lInner.getY() - lOuter.getY()) / yInc;
            }
        });


        lOuter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        y0 = motionEvent.getY();
                        vibrate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int thrust = (int) updateLeftView(motionEvent.getY());
                        mDriver.rectifyThrust(thrust);
                        break;
                    case MotionEvent.ACTION_UP:
                        centreLeftViews();
                        mDriver.stopThrust();
                        break;
                }

                return true;
            }
        });
    }

    private float updateLeftView(float yE) {
        float yDiff = yE - y0;
        float thrust;

        if (yDiff <= -yInc) {       // You reached the top part.
            lInner.setY(yInner - yInc * yComp);
            lInner.invalidate();

            lCentre.setY(yCentre - yInc);
            lCentre.invalidate();

            thrust = 5;

        } else if (yDiff >= yInc) { // You reached the bottom part.
            lInner.setY(yInner + yInc * yComp);
            lInner.invalidate();

            lCentre.setY(yCentre + yInc);
            lCentre.invalidate();

            thrust = -5;
        } else {
            lInner.setY(yInner + yDiff * yComp);
            lInner.invalidate();

            lCentre.setY(yCentre + yDiff);
            lCentre.invalidate();

            thrust = map(yDiff, -yInc, yInc, Driver.MAX_THRUST_FORWARD, Driver.MAX_THRUST_BACKWARD);
        }

        return thrust;
    }

    private float map(float x, float inMin, float inMax, float outMin, float outMax) {
        return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    private void centreLeftViews() {
        lInner.setY(yInner);
        lInner.invalidate();

        lCentre.setY(yCentre);
        lCentre.invalidate();
    }

    private void vibrate() {
        Vibrator v = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(75);
        }
    }

    private float xInner, xCentre;
    private float xInc, xComp;
    private float x0;

    @SuppressLint("ClickableViewAccessibility")
    private void setViewsForRightJoystick(View view) {
        final ImageView rOuter = view.findViewById(R.id.rOuter);
        rInner = view.findViewById(R.id.rInner);
        rCentre = view.findViewById(R.id.rCenter);

        rCentre.post(new Runnable() {
            @Override
            public void run() {
                xInner = rInner.getX();
                xCentre = rCentre.getX();

                xInc = rCentre.getX() - rOuter.getX();
                xComp = (rInner.getX() - rOuter.getX()) / xInc;
            }
        });

        rOuter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        //JoystickFragment.viewPager.disable();
                        x0 = motionEvent.getX();
                        vibrate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int steering = (int) updateRightViews(motionEvent.getX());
                        mDriver.rectifySteering(steering);
                        break;
                    case MotionEvent.ACTION_UP:
                        //JoystickFragment.viewPager.enable();
                        centreRightPositions();
                        mDriver.centerSteering();
                }

                return true;
            }
        });
    }

    private float updateRightViews(float xE) {
        float xDiff = xE - x0;
        float steering;

        if (xDiff < -xInc) {       // You reached the left most.
            rInner.setX(xInner - xInc * xComp);
            rInner.invalidate();

            rCentre.setX(xCentre - xInc);
            rCentre.invalidate();

            steering = -5;

        } else if (xDiff > xInc) { // Your reached the right most.
            rInner.setX(xInner + xInc * xComp);
            rInner.invalidate();

            rCentre.setX(xCentre + xInc);
            rCentre.invalidate();

            steering = 5;
        } else {
            rInner.setX(xInner + xDiff * xComp);
            rInner.invalidate();

            rCentre.setX(xCentre + xDiff);
            rCentre.invalidate();

            steering = map(xDiff, -xInc, xInc, Driver.MAX_STEERING_LEFT, Driver.MAX_STEERING_RIGHT);
        }

        return steering;
    }

    private void centreRightPositions() {
        rInner.setX(xInner);
        rInner.invalidate();

        rCentre.setX(xCentre);
        rCentre.invalidate();
    }

    private void toastMessage(int resourceId) {
        String message = mContext.getResources().getString(resourceId);

        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
