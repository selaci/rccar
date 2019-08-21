package com.kerberosns.rccar.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
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

    private float yLeftO, yLeftIIni, yLeftIComp, yLeftCInit;
    private float yLeftCLowest, yLeftCHighest, yLeftCVirtPos;
    private float y;

    @SuppressLint("ClickableViewAccessibility")
    private void setViewsForLeftJoystick(View view) {
        final ImageView lOuter = view.findViewById(R.id.lOuter);
        lOuter.post(new Runnable() {
            @Override
            public void run() {
                yLeftO = lOuter.getY();
            }
        });

        lInner = view.findViewById(R.id.lInner);
        lInner.post(new Runnable() {
            @Override
            public void run() {
                yLeftIIni = lInner.getY();
                yLeftIComp = (yLeftIIni - yLeftO) / (lCentre.getY() - yLeftO);
            }
        });

        lCentre = view.findViewById(R.id.lCentre);
        lCentre.post(new Runnable() {
            @Override
            public void run() {
                yLeftCLowest = yLeftO;
                yLeftCHighest = yLeftO + lOuter.getHeight() - lCentre.getHeight();
                yLeftCVirtPos = lCentre.getY();
                yLeftCInit = lCentre.getY();
            }
        });

        lOuter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        vibrate();
                        y = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateLeftPositions(event.getY());
                        rectifyThrust();
                        break;
                    case MotionEvent.ACTION_UP:
                        moveLeftToInitial();
                        stopThrust();
                        break;
                }
                return true;
            }
        });
    }

    private void vibrate() {
        Vibrator v = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(75);
        }
    }

    private void updateLeftPositions(float yE) {
        float yCDiff, yCPos;

        yCDiff = yE - y;
        yLeftCVirtPos += yCDiff;

        if (yLeftCVirtPos < yLeftCLowest) {
            yCPos = yLeftCLowest;
        } else if (yLeftCVirtPos > yLeftCHighest) {
            yCPos = yLeftCHighest;
        } else {
            yCPos = yLeftCVirtPos;
        }

        y = yE;
        lCentre.setY(yCPos);
        lCentre.invalidate();

        float yIDiff = (yCPos - yLeftCInit) * yLeftIComp;
        lInner.setY(yLeftIIni + yIDiff);
        lInner.invalidate();
    }

    private void moveLeftToInitial() {
        yLeftCVirtPos = yLeftCInit;
        lCentre.setY(yLeftCInit);
        lInner.setY(yLeftIIni);
        lInner.invalidate();
    }

    private float xRightO, xRightIIni, xRightIComp, xRightCInit;
    private float xRightCLowest, xRightCHighest, xRightCVirtPos;
    private float x;

    @SuppressLint("ClickableViewAccessibility")
    private void setViewsForRightJoystick(View view) {
        final ImageView rOuter = view.findViewById(R.id.rOuter);
        rOuter.post(new Runnable() {
            @Override
            public void run() {
                xRightO = rOuter.getX();
            }
        });

        rInner = view.findViewById(R.id.rInner);
        rInner.post(new Runnable() {
            @Override
            public void run() {
                xRightIIni = rInner.getX();
                xRightIComp = (xRightIIni - xRightO) / (rCentre.getX() - xRightO);
            }
        });

        rCentre = view.findViewById(R.id.rCenter);
        rCentre.post(new Runnable() {
            @Override
            public void run() {
                xRightCLowest = xRightO;
                xRightCHighest = xRightO + rOuter.getWidth() - rCentre.getWidth();
                xRightCVirtPos = rCentre.getX();
                xRightCInit = rCentre.getX();
            }
        });

        rOuter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        vibrate();
                        x = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateRightPositions(event.getX());
                        rectifySteering();
                        break;
                    case MotionEvent.ACTION_UP:
                        moveRightToInitial();
                        centerSteering();
                        break;
                }
                return true;
            }
        });
    }

    private void updateRightPositions(float xE) {
        float xCDiff, xCPos;

        xCDiff = xE - x;
        xRightCVirtPos += xCDiff;

        if (xRightCVirtPos < xRightCLowest) {
            xCPos = xRightCLowest;
        } else if (xRightCVirtPos > xRightCHighest) {
            xCPos = xRightCHighest;
        } else {
            xCPos = xRightCVirtPos;
        }

        x = xE;
        rCentre.setX(xCPos);
        rCentre.invalidate();

        float xIDiff = (xCPos - xRightCInit) * xRightIComp;
        rInner.setX(xRightIIni + xIDiff);
        rInner.invalidate();
    }

    private void moveRightToInitial() {
        xRightCVirtPos = xRightCInit;
        rCentre.setX(xRightCInit);
        rInner.setX(xRightIIni);
        rInner.invalidate();
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
