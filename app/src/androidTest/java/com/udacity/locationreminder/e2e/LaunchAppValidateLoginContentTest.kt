package com.udacity.locationreminder.e2e

import android.view.KeyEvent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LaunchAppValidateLoginContentTest {

    private lateinit var device: UiDevice
    private val locationReminderPackage = "com.udacity.locationreminder"

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun openLocationReminderApp() {
        device.pressHome()

        device.assertLauncherPackageWaitAppLaunch(locationReminderPackage)

        launchApp(appPackageName = locationReminderPackage)

        /** Wait for the app to appear */
        device.waitUntilScreenLoads(currentAppPackage = locationReminderPackage)

        // Verify the test is displayed in the Ui
        device.waitUntilVisibleAndAssertHasText(
            viewId = "textWelcome",
            text = "Location Reminder",
            currentAppPackage = locationReminderPackage)

        device.waitUntilVisibleAndAssertHasText(
            viewId = "textWelcomeDescription",
            text = "Set your mind free to what matters most and do the things at the right time and place.",
            currentAppPackage = locationReminderPackage)

        device.waitUntilVisibleAndAssertHasText(
            viewId = "buttonLogin",
            text = "LOGIN",
            currentAppPackage = locationReminderPackage
        )
    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
    private fun getLauncherPackageName(): String? {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val packageManager = ApplicationProvider.getApplicationContext<Context>().packageManager
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) as ResolveInfo
        return resolveInfo.activityInfo?.packageName
    }
}