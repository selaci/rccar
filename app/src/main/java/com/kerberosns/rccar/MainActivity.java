package com.kerberosns.rccar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.kerberosns.rccar.fragments.DevicesFragment;
import com.kerberosns.rccar.fragments.JoystickFragment;
import com.kerberosns.rccar.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity implements
        DevicesFragment.Selectable, SettingsFragment.Configurable {

    public static final String APPLICATION = "RC_CAR";
    public static String SETTINGS = "SETTINGS";

    private DrawerLayout mDrawer;
    private Toolbar mToolbar;

    private ActionBarDrawerToggle drawerToggle;

    private Settings mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);

        NavigationView navView = findViewById(R.id.nav_view);
        setupDrawerLayout(navView);

        mSettings = new Settings();

        FragmentManager fm = getSupportFragmentManager();
        if (fm.getFragments().size() == 0) {
            goToDevices();
        } else {
            for (Fragment fragment : fm.getFragments()) {
                fm.beginTransaction().replace(R.id.frame_layout, fragment, null).commit();
            }
        }
    }

    public ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, mToolbar,
                R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerLayout(NavigationView navView) {
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.devices:
                goToDevices();
                break;
            case R.id.settings:
                goToSettings();
                break;
        }

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());

        mDrawer.closeDrawers();
    }

    private void goToDevices() {
        clearFragmentsBackStack();
        Fragment fragment = DevicesFragment.newInstance(mSettings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment, null)
                .commit();
    }

    private void clearFragmentsBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
    }

    @Override
    public void onDeviceSelected(BluetoothDevice bluetoothDevice) {
        goToJoystick(bluetoothDevice);
    }

    private final static int REQUEST_ENABLE_BLUETOOTH = 0;

    @Override
    public void onRequestBluetoothEnabled() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode != RESULT_OK) {
                toastMessage(getResources().getString(R.string.bluetooth_not_enabled));
            } else {
                Fragment fragment = DevicesFragment.newInstance(mSettings);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit();
            }
        }
    }

    private void goToSettings() {
      clearFragmentsBackStack();
      Fragment fragment = SettingsFragment.newInstance(mSettings);

      getSupportFragmentManager()
               .beginTransaction()
               .replace(R.id.frame_layout, fragment, null)
               .commit();
    }

    private void goToJoystick(BluetoothDevice bluetoothDevice) {
      Fragment fragment = JoystickFragment.newInstance(mSettings, bluetoothDevice);
      getSupportFragmentManager()
               .beginTransaction()
               .replace(R.id.frame_layout, fragment, null)
               .addToBackStack(null)
               .commit();
    }

    @Override
    public void onDevelopmentModeChange(boolean enabled) {
        mSettings.setDevelopmentMode(enabled);
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
