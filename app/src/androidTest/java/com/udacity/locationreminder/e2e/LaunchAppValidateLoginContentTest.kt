package com.udacity.locationreminder.e2e

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import kotlinx.coroutines.currentCoroutineContext
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
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

        val launcherPackage = getLauncherPackageName()
        MatcherAssert.assertThat(launcherPackage, Matchers.notNullValue())
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), 5000)

        /** Launch the App */
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent: Intent? = context.packageManager.getLaunchIntentForPackage(locationReminderPackage)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        /** Wait for the app to appear */
        device.waitUltiScreenLoads(currentAppPackage = locationReminderPackage)

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