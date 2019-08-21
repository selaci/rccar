package com.kerberosns.rccar;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SettingsFragmentTest {
    private Settings mSettings;

    @Before
    public void setUp() {
        mSettings = new Settings();
    }

    @Test
    public void defaultsToNonDevelopmentMode() {
        assertFalse(mSettings.isDevelopmentMode());
    }

    @Test
    public void canChangeTheDevelopmentMode() {
        mSettings.setDevelopmentMode(true);

        assertTrue(mSettings.isDevelopmentMode());
    }
}
