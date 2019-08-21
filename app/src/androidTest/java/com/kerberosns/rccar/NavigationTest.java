package com.kerberosns.rccar;

import android.content.pm.ActivityInfo;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.Espresso.pressBackUnconditionally;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class NavigationTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    /**
     * Go through some fragments and verify that even after changing orientation all works as
     * expected.
     */
    @Test
    public void navigate() {
        // Load main activity with "devices" as default fragment.
        onView(withId(R.id.devices)).check(matches(isDisplayed()));

        // Click on a device.
        onView(withId(R.id.devices)).perform(click());

        // Verify you are at JoystickFragment.
        onView(withId(R.id.control)).check(matches(isDisplayed()));

        // Change orientation.
        mActivityRule.getActivity()
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Go back.
        pressBack();

        onView(withId(R.id.devices)).check(matches(isDisplayed()));

        // Open the menu.
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()).check(matches(isOpen()));

        // Go to settings.
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.settings));

        // Verify you are on settings.
        onView(withId(R.id.settings)).check(matches(isDisplayed()));

        // Finish the application.
        pressBackUnconditionally();

        // Verify is finished.
        assertTrue(mActivityRule.getActivity().isDestroyed());
    }
}
