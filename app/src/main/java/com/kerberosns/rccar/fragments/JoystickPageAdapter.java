package com.kerberosns.rccar.fragments;

import android.bluetooth.BluetoothDevice;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.kerberosns.rccar.Settings;

public class JoystickPageAdapter extends FragmentStatePagerAdapter {
    private Settings settings;

    JoystickPageAdapter(FragmentManager fragmentManager,
                                   Settings settings, BluetoothDevice bluetoothDevice) {
        super(fragmentManager);
        this.settings = settings;
    }

    public Fragment getItem(int item) {
        if (item == 0) {
            return JoystickDriverFragment.newInstance(settings);
        } else {
            return JoystickSettingsFragment.newInstance(settings);
        }
    }

    public int getCount() {
        return 2;
    }
}
