package com.udacity.locationreminder.e2e

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.udacity.locationreminder.BuildConfig
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class AutomatorFullFlow {

    private lateinit var device: UiDevice
    private val locationReminderPackage = "com.udacity.locationreminder"

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    /**
     * This method will be break down into small reusable code
     * */
    @Test
    fun launchApp_signup_permissions_addreminder_editReminder_deleteAccount() {
        // Context of the app under test.
        val appPackage = InstrumentationRegistry.getInstrumentation().targetContext.packageName

        device.pressHome()

        /** Launch the App */
        val context: Context = getApplicationContext()
        val intent: Intent? = context.packageManager
            .getLaunchIntentForPackage(locationReminderPackage)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        /** Wait for the app to appear */
        device.wait(Until.hasObject(By.pkg(locationReminderPackage)), 5000)

        // TODO - Check how to properly remove permission before to re-run the flow test
        //device.executeShellCommand("adb shell pm revoke ${locationReminderPackage}.debug android.permission.ACCESS_FINE_LOCATION")
        //device.executeShellCommand("adb shell pm revoke ${locationReminderPackage}.debug android.permission.ACCESS_COARSE_LOCATION")
        //device.executeShellCommand("adb shell pm revoke ${locationReminderPackage}.debug android.permission.ACCESS_BACKGROUND_LOCATION")
        //device.executeShellCommand("adb shell pm reset-permissions")

        /** Login for new Users */
        device.onViewContainsTextClickAndWait(text = "LOGIN")

        device.onViewContainsTextClickAndWait(text = "Sign in with email")

        device.onViewWithIdPerformTypeText(
            viewId = "email",
            text = BuildConfig.SAMPLE_USER_EMAIL,
            currentAppPackage = locationReminderPackage
        )

        device.onViewContainsTextClickAndWait(text = "NEXT")

        device.onViewWithIdPerformTypeText(
            viewId = "name",
            text = BuildConfig.SAMPLE_USER_NAME,
            currentAppPackage = locationReminderPackage
        )

        device.onViewWithIdPerformTypeText(
            viewId = "password",
            text = BuildConfig.SAMPLE_USER_PASS,
            currentAppPackage = locationReminderPackage
        )

        device.onViewContainsTextClickAndWait(text = "SAVE")

        device.onViewWithIdPerformSwipeLeft(
            viewId = "onBoardingStepImage",
            numberOfSwipes = 2,
            currentAppPackage = locationReminderPackage
        )

        device.onViewWithIdClickAndWait(
            viewId = "onboardingButtonStart",
            currentAppPackage = locationReminderPackage
        )

        /**  ADDd REMINDER WITH PERMISSIONS REQUESTS */

        device.onViewWithIdClickAndWait(
            viewId = "actionButtonAddReminder",
            currentAppPackage = locationReminderPackage
        )

        device.onViewContainsTextClickAndWait(text = "SELECT LOCATION")

        device.onViewContainsTextClickAndWait(text = "Allow only while using the app")

        device.onViewContainsTextClickAndWait(text = "GET THIS LOCATION")

        device.onViewWithIdPerformTypeText(
            viewId = "reminderLocationName",
            text = "Work",
            currentAppPackage = locationReminderPackage
        )

        device.onViewWithIdPerformTypeText(
            viewId = "reminderTitle",
            text = "Get my umbrella",
            currentAppPackage = locationReminderPackage
        )

        device.onViewWithIdPerformTypeText(
            viewId = "reminderDescription",
            text = "Set location alert to never forget again",
            currentAppPackage = locationReminderPackage
        )

        // Todo - Make a better reuse of this
        device.swipe(
            device.displayWidth / 2,
            device.displayHeight / 2,
            device.displayWidth / 2,
            device.displayHeight / 4,
            10
        )

        device.swipe(
            device.displayWidth / 2,
            device.displayHeight / 2,
            device.displayWidth / 2,
            device.displayHeight / 4,
            10
        )

        device.onViewWithIdPerformTypeText(
            viewId = "expirationDurationEditText",
            text = "5",
            currentAppPackage = locationReminderPackage
        )

        device.onViewWithTextClickAndWait(text = "SAVE REMINDER")

        device.onViewWithIdClickAndWait(viewId = "android:id/button1")

        device.onViewContainsTextClickAndWait(text = "Permissions")

        device.onViewTextMatchesClickAndWait(text = "Only while app is in use")

        device.onViewTextMatchesClick(text = "Allow all the time")

        device.pressBack()
        device.pressBack()
        device.pressBack()

        device.onViewContainsTextClickAndWait(text = "SAVE REMINDER")

        /** Click on AppBar menu action */
        val topAppBar = device.findObject(UiSelector().resourceId("$appPackage:id/topAppBar"))
        device.click(topAppBar.bounds.width() - 20, 100)

        device.onViewContainsTextClickAndWait(text = "Copyrights")

        device.assertViewHasText(
            viewId = "labelSource",
            text = "Source",
            currentAppPackage = locationReminderPackage
        )

        device.assertViewHasText(
            viewId = "labelAuthor",
            text = "Author",
            currentAppPackage = locationReminderPackage
        )

        device.assertViewHasText(
            viewId = "copyrightSource",
            text = "Lottie Files",
            currentAppPackage = locationReminderPackage
        )

        device.pressBack()

        device.onViewContainsTextClickAndWait(text = "Get my umbrella")

        device.assertViewHasText(
            viewId = "reminderTitle",
            text = "Get my umbrella",
            currentAppPackage = locationReminderPackage
        )

        device.assertViewHasText(
            viewId = "reminderDescription",
            text = "Set location alert to never forget again",
            currentAppPackage = locationReminderPackage
        )

        device.assertViewHasText(
            viewId = "reminderLocationName",
            text = "Work",
            currentAppPackage = locationReminderPackage
        )

        device.assertViewHasText(
            viewId = "textCurrentCircularRadius",
            text = "50 meters",
            currentAppPackage = locationReminderPackage
        )

        device.assertViewHasText(
            viewId = "textGeofenceStatus",
            text = "Geofence is disabled",
            currentAppPackage = locationReminderPackage
        )

        device.onViewContainsTextClickAndWait(text = "EDIT")

        device.onViewWithIdPerformTypeText(
            viewId = "reminderLocationName",
            text = "At work",
            currentAppPackage = locationReminderPackage
        )

        device.onViewWithIdPerformTypeText(
            viewId = "reminderTitle",
            text = "Get my new umbrella",
            currentAppPackage = locationReminderPackage
        )

        device.onViewWithIdPerformTypeText(
            viewId = "reminderDescription",
            text = "Set reminder alert to never forget it again",
            currentAppPackage = locationReminderPackage
        )

        device.swipe(
            device.displayWidth / 2,
            device.displayHeight / 2,
            device.displayWidth / 2,
            device.displayHeight / 4,
            10
        )

        device.swipe(
            device.displayWidth / 2,
            device.displayHeight / 2,
            device.displayWidth / 2,
            device.displayHeight / 4,
            10
        )

        device.onViewWithTextClick("When I exit")

        device.onViewWithIdPerformClick(
            viewId = "isGeofenceEnableSwitch",
            currentAppPackage = locationReminderPackage
        )

        device.onViewContainsTextClickAndWait(text = "SAVE REMINDER")

        device.assertViewHasText(
            viewId = "reminderTitle",
            text = "Get my new umbrella",
            currentAppPackage = locationReminderPackage
        )

        device.assertViewHasText(
            viewId = "reminderDescription",
            text = "Set reminder alert to never forget it again",
            currentAppPackage = locationReminderPackage
        )

        device.assertViewHasText(
            viewId = "reminderLocationName",
            text = "At work",
            currentAppPackage = locationReminderPackage
        )

        device.assertViewHasText(
            viewId = "textCurrentCircularRadius",
            text = "50 meters",
            currentAppPackage = locationReminderPackage
        )

        device.assertViewHasText(
            viewId = "textGeofenceStatus",
            text = "Geofence is enabled",
            currentAppPackage = locationReminderPackage
        )

        device.pressBack()

        /** Delete reminder from the list */

        device.onViewWithIdClickAndWait(
            viewId = "imageDeleteReminder",
            currentAppPackage = locationReminderPackage
        )

        device.onViewWithTextClick(text = "DELETE")

        /** Delete Account validate login */

        device.click(topAppBar.bounds.width() - 20, 100)

        device.onViewContainsTextClickAndWait(text = "Delete account")

        device.onViewContainsTextClickAndWait(text = "DELETE ACCOUNT")

        /** Validate Login screen after account is deleted */

        device.assertViewHasText(
            viewId = "textWelcome",
            text = "Location Reminder",
            currentAppPackage = locationReminderPackage
        )

        device.assertViewHasText(
            viewId = "textWelcomeDescription",
            text = "Set your mind free to what matters most and do the things at the right time and place.",
            currentAppPackage = locationReminderPackage
        )

        device.assertViewHasText(
            viewId = "buttonLogin",
            text = "LOGIN",
            currentAppPackage = locationReminderPackage
        )
    }
}