package com.udacity.locationreminder.e2e

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.udacity.locationreminder.BuildConfig
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
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
    fun launchApp_signup_permissions_addreminder_deleteAccount() {
        // Context of the app under test.
        val appPackage = InstrumentationRegistry.getInstrumentation().targetContext.packageName

        device.pressHome()

        /** Launch the App */
        val context: Context = getApplicationContext()
        val intent: Intent? = context.packageManager.getLaunchIntentForPackage(locationReminderPackage)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        /** Wait for the app to appear */
        device.wait(Until.hasObject(By.pkg(locationReminderPackage)), 5000)

        /** Login for new Users */
        val loginButtonAction: UiObject = device.findObject(UiSelector().textContains("LOGIN"))
        loginButtonAction.clickAndWaitForNewWindow()

        val signInWithEmailButtonAction: UiObject =
            device.findObject(UiSelector().textContains("Sign in with email"))
        signInWithEmailButtonAction.clickAndWaitForNewWindow()

        device.findObject(By.res(appPackage, "email")).text = BuildConfig.SAMPLE_USER_EMAIL

        val nextButtonAction: UiObject = device.findObject(UiSelector().textContains("NEXT"))
        nextButtonAction.clickAndWaitForNewWindow()

        device.findObject(By.res(appPackage, "name")).text =  BuildConfig.SAMPLE_USER_NAME
        device.findObject(By.res(appPackage, "password")).text =  BuildConfig.SAMPLE_USER_PASS

        val saveButtonAction: UiObject = device.findObject(UiSelector().textContains("SAVE"))
        saveButtonAction.clickAndWaitForNewWindow()

        val onBoardingStepImage = device.findObject(UiSelector().resourceId("$appPackage:id/onBoardingStepImage"))
        // int startX, int startY, int endX, int endY, int steps
        with(onBoardingStepImage) {
            device.swipe(bounds.right - 5, bounds.centerY(), bounds.left / 4, bounds.centerY(), 10)
            device.swipe(bounds.right - 5, bounds.centerY(), bounds.left / 4, bounds.centerY(), 10)
        }

        val onboardingButtonSkipOrStart = device.findObject(UiSelector().resourceId("$appPackage:id/onboardingButtonSkip"))
        onboardingButtonSkipOrStart.clickAndWaitForNewWindow()

        /**  ADDd REMINDER WITH PERMISSIONS REQUESTS */

        val actionButtonAddReminder = device.findObject(UiSelector().resourceId("$appPackage:id/actionButtonAddReminder"))
        actionButtonAddReminder.clickAndWaitForNewWindow()

        val textSelectedLocation: UiObject = device.findObject(UiSelector().textContains("SELECT LOCATION"))
        textSelectedLocation.clickAndWaitForNewWindow()

        val confirmLocationPermission: UiObject = device.findObject(UiSelector().textContains("Allow only while using the app"))
        confirmLocationPermission.clickAndWaitForNewWindow()

        val textGetThisLocation: UiObject = device.findObject(UiSelector().textContains("GET THIS LOCATION"))
        textGetThisLocation.clickAndWaitForNewWindow()

        device.findObject(By.res(appPackage, "reminderLocationName")).text = "Work"
        device.findObject(By.res(appPackage, "reminderTitle")).text = "Get my umbrella"
        device.findObject(By.res(appPackage, "reminderDescription")).text = "Set location alert to never forget again"

        device.swipe(device.displayWidth / 2, device.displayHeight / 2, device.displayWidth / 2, device.displayHeight / 4, 10)

        device.findObject(By.res(appPackage, "expirationDurationEditText")).text = "5"

        device.findObject(By.res(appPackage, "isGeofenceEnableSwitch")).click()

        val buttonSaveReminder: UiObject = device.findObject(UiSelector().textContains("SAVE REMINDER"))
        buttonSaveReminder.clickAndWaitForNewWindow()

        val settingAction: UiObject =device.findObject(UiSelector().resourceId("android:id/button1"))
        settingAction.clickAndWaitForNewWindow()

        val permissionsAction: UiObject = device.findObject(UiSelector().textContains("Permissions"))
        permissionsAction.clickAndWaitForNewWindow()

        val permissionsLocationAction: UiObject = device.findObject(UiSelector().textMatches("Only while app is in use"))
        permissionsLocationAction.clickAndWaitForNewWindow()

        device.findObject(UiSelector().textMatches("Allow all the time")).click()

        device.pressBack()
        device.pressBack()
        device.pressBack()

        buttonSaveReminder.clickAndWaitForNewWindow()

        /** Click on AppBar menu action */
        val topAppBar = device.findObject(UiSelector().resourceId("$appPackage:id/topAppBar"))
        device.click(topAppBar.bounds.width() - 20, 100)

        val menuItm = device.findObject(UiSelector().textContains("Delete account"))
        menuItm.clickAndWaitForNewWindow()

        val deleteAccountAction: UiObject = device.findObject(UiSelector().textContains("DELETE ACCOUNT"))
        deleteAccountAction.clickAndWaitForNewWindow()

        // Verify the test is displayed in the Ui
        val textWelcome: UiObject2 = device.wait(Until.findObject(By.res(locationReminderPackage, "textWelcome")), 1000)
        val textWelcomeDescription: UiObject2 = device.wait(Until.findObject(By.res(locationReminderPackage, "textWelcomeDescription")), 1000)
        val loginButton: UiObject2 = device.wait(Until.findObject(By.res(locationReminderPackage, "buttonLogin")), 1000)
        MatcherAssert.assertThat(textWelcome.text, CoreMatchers.`is`(CoreMatchers.equalTo("Location Reminder")))
        MatcherAssert.assertThat(textWelcomeDescription.text, CoreMatchers.`is`(CoreMatchers.equalTo("Set your mind free to what matters most and do the things at the right time and place.")))
        MatcherAssert.assertThat(loginButton.text, CoreMatchers.`is`(CoreMatchers.equalTo("LOGIN")))
    }
}