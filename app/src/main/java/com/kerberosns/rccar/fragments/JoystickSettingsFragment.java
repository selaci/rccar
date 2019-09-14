package com.kerberosns.rccar.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

import com.kerberosns.rccar.MainActivity;
import com.kerberosns.rccar.R;
import com.kerberosns.rccar.Settings;
import com.kerberosns.rccar.rccar.Driver;


// TODO: Use settings.s
public class JoystickSettingsFragment extends Fragment {
    private Context mContext;
    private Driver mDriver;

    static Fragment newInstance(Settings settings) {
        Fragment fragment = new JoystickSettingsFragment();
        Bundle arguments = new Bundle();

        arguments.putParcelable(MainActivity.SETTINGS, settings);

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
        setRetainInstance(true);

        Bundle arguments = getArguments();
        Settings settings = arguments.getParcelable(MainActivity.SETTINGS);
        mDriver = new Driver(JoystickFragment.mDeviceManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.joystick_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        SeekBar seekBarLeft = view.findViewById(R.id.seekBarLeft);
        seekBarLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mDriver.setLeftSpeed(seekBar.getProgress());
            }
        });

        SeekBar seekBarRight = view.findViewById(R.id.seekBarRight);
        seekBarRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mDriver.setRightSpeed(seekBar.getProgress());
            }
        });
    }
}
