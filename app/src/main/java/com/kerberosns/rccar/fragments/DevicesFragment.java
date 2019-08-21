package com.kerberosns.rccar.fragments;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kerberosns.rccar.Adapter;
import com.kerberosns.rccar.R;
import com.kerberosns.rccar.Settings;
import com.kerberosns.rccar.bluetooth.BluetoothBroadcastReceiver;
import com.kerberosns.rccar.bluetooth.BluetoothManager;
import com.kerberosns.rccar.bluetooth.BluetoothManagerFactory;
import com.kerberosns.rccar.bluetooth.Device;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.kerberosns.rccar.MainActivity.SETTINGS;

public class DevicesFragment extends Fragment implements Adapter.OnDeviceClickListener {
    public interface Selectable {
        void onDeviceSelected(Device device);
        void onRequestBluetoothEnabled();
    }

    private Selectable mListener;
    private Context mContext;
    private BluetoothManager mBluetoothManager;
    private Adapter mAdapter;
    private Button mDiscoverButton;
    private Handler mHandler;
    private BluetoothBroadcastReceiver mBroadcastReceiver;
    private List<Device> mDevices = new ArrayList<>();

    public static Fragment newInstance(Settings settings) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SETTINGS, settings);

        Fragment fragment = new DevicesFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        if (context instanceof Selectable) {
            mListener = (Selectable) context;
        } else {
            throw new ClassCastException("Selectable interface not implemented.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mHandler = new MyHandler(new WeakReference<>(this));

        mBroadcastReceiver = new BluetoothBroadcastReceiver();
        mBroadcastReceiver.addActionListener(new BluetoothBroadcastReceiver.ActionListener() {
            @Override
            public void onBluetoothDeviceFound(BluetoothDevice device) {
                addBluetoothDevice(device);
            }
        });

        mBluetoothManager = getBluetoothManager();
        mBluetoothManager.openProfile();
        if (!mBluetoothManager.isSupported()) {
            toastMessage(getResources().getString(R.string.not_supported));
        } else if (!mBluetoothManager.isEnabled()) {
            mListener.onRequestBluetoothEnabled();
        }
    }


    private void addBluetoothDevice(BluetoothDevice device) {
        if (!inTheList(device)) {
            mDevices.add(
                    new Device(
                            device.getName(),
                            device.getAddress(),
                            device.getBondState() == BluetoothDevice.BOND_BONDED));
            mAdapter.notifyDataSetChanged();
        }
    }

    private boolean inTheList(BluetoothDevice device) {
        boolean found = false;

        for(Device _device : mDevices) {
            if (_device.getName().equals(device.getName())) {
                found = true;
                break;
            }
        }

        return found;
    }

    @SuppressWarnings("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.devices, container, false);

        mDevices = mBluetoothManager.getDevices();
        mAdapter = new Adapter(mDevices, getResources(), this);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(mContext.getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        mDiscoverButton = view.findViewById(R.id.discover);
        mDiscoverButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                startDiscovery();
                return false;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mBroadcastReceiver.register(getActivity());
        mDevices = getDevices();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBroadcastReceiver.unregister(getActivity());
        endDiscovery();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothManager.closeProfile();
    }

    private BluetoothManager getBluetoothManager() {
        if (getArguments() != null) {
            Settings settings = getArguments().getParcelable(SETTINGS);

            if (settings != null) {
                return BluetoothManagerFactory.newInstance(settings, mContext);
            }
        }

        /*
         * This point should not happen. The logic in the code always forces a settings
         * arguments.
         */
        throw new RuntimeException("Settings arguments missing.");
    }

    private List<Device> getDevices() {
        mBluetoothManager.openProfile();
        return mBluetoothManager.getDevices();
    }

    private void toastMessage(String message) {
        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    @Override
    public void onClick(final Device device) {
        // The device is bounded already.
        endDiscovery();
        if (device.isPaired()) {
            mListener.onDeviceSelected(device);
        } else {
            // TODO: At the moment all devices are paired. I don't know how the bond works.
            createBond(device);
        }
    }

    private void createBond(Device device) {
        try {
            mBluetoothManager.createBond(device);
        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }

    private void startDiscovery() {
        if (mBluetoothManager.isDiscovering()) {
            mBluetoothManager.cancelDiscovery();
        }

        toastMessage(getResources().getString(R.string.discovery_started));
        mDiscoverButton.setPressed(true);
        mDiscoverButton.setEnabled(false);

        mBluetoothManager.startDiscovery();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.obtainMessage(Messages.END_DISCOVERY).sendToTarget();

            }
        }, 15000);
    }

    private void endDiscovery() {
        if (mBluetoothManager.isDiscovering()) {
            mBluetoothManager.cancelDiscovery();
            toastMessage(getResources().getString(R.string.discovery_ended));
        }

        mDiscoverButton.setPressed(false);
        mDiscoverButton.setEnabled(true);
    }

    private static class MyHandler extends Handler {
        private final WeakReference<DevicesFragment> mDevicesFragment;

        MyHandler(WeakReference<DevicesFragment> devicesFragment) {
            mDevicesFragment = devicesFragment;
        }

        @Override
        public void handleMessage(@NonNull Message message) {
            DevicesFragment fragment = mDevicesFragment.get();

            if (message.what == Messages.END_DISCOVERY) {
                fragment.endDiscovery();
            }
        }
    }

    interface Messages {
        int END_DISCOVERY = 0;
    }

}
