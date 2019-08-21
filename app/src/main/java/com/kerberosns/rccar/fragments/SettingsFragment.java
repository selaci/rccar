package com.kerberosns.rccar.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kerberosns.rccar.MainActivity;
import com.kerberosns.rccar.R;
import com.kerberosns.rccar.Settings;

import static com.kerberosns.rccar.MainActivity.SETTINGS;

public class SettingsFragment extends Fragment {
    public interface Configurable {
        void onDevelopmentModeChange(boolean enabled);
    }

    private Configurable mConfigurable;

    public static Fragment newInstance(Settings settings) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SETTINGS, settings);

        Fragment fragment = new SettingsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Configurable) {
            mConfigurable = (Configurable) context;
        } else {
            throw new ClassCastException("Configurable interface not implemented.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings, container, false);

        Switch _switch = view.findViewById(R.id.developmentMode);
        _switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mConfigurable.onDevelopmentModeChange(b);
            }
        });

        if (getArguments() != null) {
            Settings settings = getArguments().getParcelable(MainActivity.SETTINGS);
            if (settings != null) {
                _switch.setChecked(settings.isDevelopmentMode());
            }
        }

        return view;
    }
}
