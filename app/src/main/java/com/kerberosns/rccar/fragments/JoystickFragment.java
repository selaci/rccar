package com.kerberosns.rccar.fragments;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;


import com.google.android.material.tabs.TabLayout;
import com.kerberosns.rccar.MainActivity;
import com.kerberosns.rccar.R;
import com.kerberosns.rccar.Settings;
import com.kerberosns.rccar.bluetooth.BluetoothDeviceManager;
import com.kerberosns.rccar.bluetooth.BluetoothDeviceManagerFactory;
import com.kerberosns.rccar.view.CustomViewPager;

import java.io.IOException;

// TODO: Find a way to enabled / disable the view pager instead of using static public.
// TODO: AsyncTask dies when dialog is displayed and a screen rotates.
// TODO: Only a single fragment should handle the bluetooth connection.
public class JoystickFragment extends Fragment {
    private Context mContext;
    private Settings mSettings;
    private BluetoothDevice mBluetoothDevice;

    static BluetoothDeviceManager mDeviceManager;
    static CustomViewPager viewPager;

    public static Fragment newInstance(Settings settings, BluetoothDevice bluetoothDevice) {

        Fragment fragment = new JoystickFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(MainActivity.SETTINGS, settings);
        arguments.putParcelable(MainActivity.BLUETOOTH_DEVICE, bluetoothDevice);
        fragment.setArguments(arguments);

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
        Bundle arguments = getArguments();

        mSettings = arguments.getParcelable(MainActivity.SETTINGS);
        mBluetoothDevice = arguments.getParcelable(MainActivity.BLUETOOTH_DEVICE);

        setRetainInstance(true);

        Bundle args = getArguments();
        Settings settings = null;
        BluetoothDevice bluetoothDevice = null;

        if (args != null) {
            settings = args.getParcelable(MainActivity.SETTINGS);
            bluetoothDevice = args.getParcelable(MainActivity.BLUETOOTH_DEVICE);

            mDeviceManager = BluetoothDeviceManagerFactory.newInstance(settings, bluetoothDevice);
        }

        if (settings == null) {
            toastMessage(R.string.settings_not_found);
        }

        if (bluetoothDevice == null) {
            toastMessage(R.string.device_not_found);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.joystick, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        viewPager = view.findViewById(R.id.joystick_view_pager);
        TabLayout tabLayout = view.findViewById(R.id.joystick_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        PagerAdapter pagerAdapter = new JoystickPageAdapter(getChildFragmentManager(),
                mSettings, mBluetoothDevice);

        viewPager.setAdapter(pagerAdapter);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_car);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_settings);

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

    private void toastMessage(int resourceId) {
        String message = mContext.getResources().getString(resourceId);

        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private void toastMessage(String message) {
        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
