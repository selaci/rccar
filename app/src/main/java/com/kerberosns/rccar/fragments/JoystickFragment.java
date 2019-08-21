package com.kerberosns.rccar.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kerberosns.rccar.MainActivity;
import com.kerberosns.rccar.R;
import com.kerberosns.rccar.Settings;
import com.kerberosns.rccar.bluetooth.BluetoothDeviceManager;
import com.kerberosns.rccar.bluetooth.BluetoothDeviceManagerFactory;
import com.kerberosns.rccar.bluetooth.Device;

import java.io.IOException;

public class JoystickFragment extends Fragment {
    private Context mContext;
    private BluetoothDeviceManager mDeviceManager;

    private static String BLUETOOTH_DEVICE = "BLUETOOTH_DEVICE";

    private ImageView lInner, lCentre;
    private ImageView rInner, rCentre;

    public static Fragment newInstance(Settings settings, Device device) {
        Bundle args = new Bundle();
        args.putParcelable(MainActivity.SETTINGS, settings);
        args.putParcelable(BLUETOOTH_DEVICE, device);

        Fragment fragment = new JoystickFragment();
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
        Device device = null;

        if (args != null) {
            settings = args.getParcelable(MainActivity.SETTINGS);
            device = args.getParcelable(BLUETOOTH_DEVICE);

            mDeviceManager = BluetoothDeviceManagerFactory.newInstance(mContext, settings, device);
        }

        if (settings == null) {
            toastMessage(R.string.settings_not_found);
        }

        if (device == null) {
            toastMessage(R.string.device_not_found);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.joystick, container, false);

        setViewsForLeftJoystick(view);
        setViewsForRightJoystick(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mDeviceManager.isConnected()) {
            new ConnectTask().execute(mDeviceManager);
        }
    }

    // TODO: This is not guaranteed to execute.
    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            mDeviceManager.disconnect();
        } catch (IOException e) {
            toastMessage(e.getMessage());
        }
    }

    private static final String OK = "OK";

    class ConnectTask extends AsyncTask<BluetoothDeviceManager, Void, String> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(mContext);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Connecting");
            mDialog.setIndeterminate(true);
            mDialog.show();
        }

        @Override
        public String doInBackground(BluetoothDeviceManager... managers) {
            try {
                managers[0].connect();
            } catch (IOException e) {
                // TODO: Send a message to the main activity / fragment via broadcast or handler.
                return e.getMessage();
            }

            return OK;
        }

        @Override
        protected void onPostExecute(String result) {
            mDialog.dismiss();
            if (!result.equals(OK)) {
                toastMessage(result);
            }
        }
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
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateLeftView(motionEvent.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        centreLeftViews();
                        break;
                }
                return true;
            }
        });
    }

    private void updateLeftView(float yE) {
        float yDiff = yE - y0;

        if (yDiff <= -yInc) {       // You reached the top part.
            lInner.setY(yInner - yInc * yComp);
            lInner.invalidate();

            lCentre.setY(yCentre - yInc);
            lCentre.invalidate();
        } else if (yDiff >= yInc) { // You reached the bottom part.
            lInner.setY(yInner + yInc * yComp);
            lInner.invalidate();

            lCentre.setY(yCentre + yInc);
            lCentre.invalidate();
        } else {
            lInner.setY(yInner + yDiff * yComp);
            lInner.invalidate();

            lCentre.setY(yCentre + yDiff);
            lCentre.invalidate();
        }
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
                        x0 = motionEvent.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateRightViews(motionEvent.getX());
                        break;
                    case MotionEvent.ACTION_UP:
                        centreRightPositions();
                }
                return true;
            }
        });
    }

    private void updateRightViews(float xE) {
        float xDiff = xE - x0;

        if (xDiff < -xInc) {       // You reached the left most.
            rInner.setX(xInner - xInc * xComp);
            rInner.invalidate();

            rCentre.setX(xCentre - xInc);
            rCentre.invalidate();
        } else if (xDiff > xInc) { // Your reached the right most.
            rInner.setX(xInner + xInc * xComp);
            rInner.invalidate();

            rCentre.setX(xCentre + xInc);
            rCentre.invalidate();
        } else {
            rInner.setX(xInner + xDiff * xComp);
            rInner.invalidate();

            rCentre.setX(xCentre + xDiff);
            rCentre.invalidate();
        }
    }

    private void centreRightPositions() {
        rInner.setX(xInner);
        rInner.invalidate();

        rCentre.setX(xCentre);
        rCentre.invalidate();
    }

    private void rectifyThrust() {
        // TODO.
    }

    private void stopThrust() {
        // TODO.
    }

    private void rectifySteering() {
        // TODO.
    }

    private void centerSteering() {
        // TODO.
    }

    private void toastMessage(String message) {
        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private void toastMessage(int resourceId) {
        String message = mContext.getResources().getString(resourceId);

        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
