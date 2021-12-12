package com.udacity.locationreminder.e2e

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LaunchAppValidateLoginContentTest {

    private lateinit var device: UiDevice

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Ignore("This implementation is for later use.")
    @Test
    fun openFromSearchLocationReminderApp() {
        device.pressHome()

        device.pressSearch()

        device.searchAppName("Reminder")
    }

}