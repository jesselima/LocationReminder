package com.udacity.locationreminder.uiautomator

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleUiAutomatorInstrumentedTest {

    lateinit var device: UiDevice

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun openLocationReminder_click_item_list_then_open_edit_screen() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Log.d("==>>> ", appContext.packageName)
        // assertEquals("com.example.myapplication", appContext.packageName)

        device.pressHome()

        // Bring up the default launcher by searching for a UI component
        // that matches the content description for the launcher button.
        val clockButton: UiObject = device.findObject(UiSelector().descriptionContains("Reminder"))

        // Perform a click on the button to load the launcher.
        clockButton.clickAndWaitForNewWindow()

        val firstItem: UiObject = device.findObject(UiSelector().textContains("Google"))
        firstItem.clickAndWaitForNewWindow()

        val editButton: UiObject = device.findObject(UiSelector().textContains("Edit"))
        editButton.clickAndWaitForNewWindow()
    }

    @Test
    fun open_MyApplication_sample() {

        device.pressHome()

        val launcherPackage = getLauncherPackageName()
        MatcherAssert.assertThat(launcherPackage, Matchers.notNullValue())
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), 5000)

        /** Launch the App */
        val context: Context = getApplicationContext()
        val intent: Intent? = context.packageManager.getLaunchIntentForPackage( "com.example.myapplication")
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        /** Wait for the app to appear */
        device.wait(Until.hasObject(By.pkg("com.example.myapplication")), 5000)
    }

    @Test
    fun openLocationReminderApp() {

        val locationReminderAppPackage = "com.udacity.locationreminder"

        device.pressHome()

        val launcherPackage = getLauncherPackageName()
        MatcherAssert.assertThat(launcherPackage, Matchers.notNullValue())
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), 5000)

        /** Launch the App */
        val context: Context = getApplicationContext()
        val intent: Intent? = context.packageManager.getLaunchIntentForPackage( locationReminderAppPackage)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        /** Wait for the app to appear */
        device.wait(Until.hasObject(By.pkg(locationReminderAppPackage)), 5000)
    }

    @Test
    fun testChangeText_sameActivity() {
        // Type text and then press the button.
//        device.findObject(By.res(loginPackage, "editTextUserInput")).text = STRING_TO_BE_TYPED
//        device.findObject(By.res(loginPackage, "changeTextBt")).click()
//
//        // Verify the test is displayed in the Ui
//        val changedText: UiObject2 = device.wait(
//                Until.findObject(By.res(loginPackage, "textToBeChanged")),
//                500
//            )
//
//        Assert.assertThat(
//            changedText.text,
//            CoreMatchers.`is`(CoreMatchers.equalTo<String>(STRING_TO_BE_TYPED))
//        )
    }

    @Test
    fun testChangeText_newActivity() {
        // Type text and then press the button.
//        device.findObject(By.res(loginPackage, "editTextUserInput")).text = STRING_TO_BE_TYPED
//        device.findObject(By.res(loginPackage, "activityChangeTextBtn")).click()
//
//        // Verify the test is displayed in the Ui
//        val changedText: UiObject2 = device.wait(
//            Until.findObject(By.res(loginPackage, "show_text_view")),
//            500
//        )
//
//        Assert.assertThat(changedText.text, CoreMatchers.`is`(CoreMatchers.equalTo<String>(STRING_TO_BE_TYPED)))
    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
    private fun getLauncherPackageName(): String? {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val packageManager = getApplicationContext<Context>().packageManager
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) as ResolveInfo
        return resolveInfo.activityInfo?.packageName
    }
}