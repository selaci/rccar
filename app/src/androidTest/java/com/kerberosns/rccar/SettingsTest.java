package com.kerberosns.rccar;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4ClassRunner.class)
public class SettingsTest {
    private void waitForIt() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Ignore.
        }
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void settingsIsOffThenOnThenOff() {
        // Load main activity with "devices" as default fragment.
        onView(withId(R.id.devices)).check(matches(isDisplayed()));

        // Open the menu.
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()).check(matches(isOpen()));

        // Go to settings.
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.settings));

        // Load main activity with "devices" as default fragment.
        onView(withId(R.id.settings)).check(matches(isDisplayed()));

        // Click on the development mode switch
        onView(withId(R.id.developmentMode)).perform(click());

        // Open the menu.
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()).check(matches(isOpen()));

        // Go back to devices.
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.devices));

        // Verify we are at devices.
        onView(withId(R.id.devices)).check(matches(isDisplayed()));

        // Open the menu.
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()).check(matches(isOpen()));

        // Go to settings.
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.settings));

        // Load main activity with "devices" as default fragment.
        onView(withId(R.id.settings)).check(matches(isDisplayed()));

        // Verify is checked.
        onView(withId(R.id.developmentMode)).check(matches(isChecked()));

        // Uncheck it.
        onView(withId(R.id.developmentMode)).perform(click());

        // Open the menu.
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()).check(matches(isOpen()));

        // Go back to devices.
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.devices));

        // Verify we are at devices.
        onView(withId(R.id.devices)).check(matches(isDisplayed()));

        // Open the menu.
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()).check(matches(isOpen()));

        // Go to settings.
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.settings));

        // Verify we are at devices.
        onView(withId(R.id.settings)).check(matches(isDisplayed()));

        // Verify is not checked.
        onView(withId(R.id.developmentMode)).check(matches(isNotChecked()));
    }
}
