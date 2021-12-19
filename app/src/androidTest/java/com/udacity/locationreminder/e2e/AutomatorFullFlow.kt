package com.udacity.locationreminder.e2e

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.udacity.locationreminder.BuildConfig
import com.udacity.locationreminder.R
import com.udacity.locationreminder.util.AppViewAssertion.isTextDisplayed
import com.udacity.locationreminder.util.AppViewAssertion.isTextNotDisplayed
import com.udacity.locationreminder.util.AppViewAssertion.isViewDisplayed
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
class AutomatorFullFlow {

    private lateinit var device: UiDevice
    private lateinit var appPackage: String
    private val locationReminderPackage = "com.udacity.locationreminder"

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        // Context of the app under test.
        appPackage = InstrumentationRegistry.getInstrumentation().targetContext.packageName
        Log.d("CURRENT APP PACKAGE:", appPackage)
    }

    /**
     * The testes run in sequence to simulate user experience and due to authentication dependency.
     * The ideal scenario is the fist test to remove location permission from the App before to
     * run the full test.
     */

    @Test
    fun b_launch_app_should_display_login_screen() {
        device.launchApp(locationReminderPackage)

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

    @Test
    fun c_launchApp_should_signup_and_display_onboarding() {
        device.launchApp()

        device.onViewContainsTextClickAndWait(text = "LOGIN")

        device.onViewContainsTextClickAndWait(text = "Sign in with email")

        device.onViewWithIdPerformTypeText(
            viewId = "email",
            text = BuildConfig.SAMPLE_USER_EMAIL,
        )

        device.onViewContainsTextClickAndWait(text = "NEXT")

        device.onViewWithIdPerformTypeText(
            viewId = "name",
            text = BuildConfig.SAMPLE_USER_NAME,
        )

        device.onViewWithIdPerformTypeText(
            viewId = "password",
            text = BuildConfig.SAMPLE_USER_PASS,
        )

        device.onViewContainsTextClickAndWait(text = "SAVE")

        device.onViewWithIdPerformSwipeLeft(
            viewId = "onBoardingStepImage",
            numberOfSwipes = 2,
        )

        device.onViewWithIdClickAndWait(
            viewId = "onboardingButtonStart",
            currentAppPackage = locationReminderPackage
        )

        isTextDisplayed("LOGOUT")
    }

    @Test
    fun d_added_reminder_should_add_reminder_to_reminder_list() {
        device.launchApp()

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
        )

        device.onViewWithIdPerformTypeText(
            viewId = "reminderTitle",
            text = "Get my umbrella",
        )

        device.onViewWithIdPerformTypeText(
            viewId = "reminderDescription",
            text = "Set location alert to never forget again",
        )

        device.swipeUp()
        device.swipeUp()

        device.onViewWithIdPerformTypeText(
            viewId = "expirationDurationEditText",
            text = "5",
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
    }

    @Test
    fun e_added_reminder_should_be_displayed_on_reminder_list() {
        device.launchApp()
        isTextDisplayed("Get my umbrella")
        isTextDisplayed("Set location alert to never forget again")
        isTextDisplayed("Work")
        isViewDisplayed(R.id.imageDeleteReminder)
        isViewDisplayed(R.id.imageReminderStatus)
    }

    @Test
    fun f_reminder_item_list_clicked_should_be_displayed_on_reminder_details() {
        device.launchApp()

        device.onViewContainsTextClickAndWait(text = "Get my umbrella")

        device.assertViewHasText(
            viewId = "reminderTitle",
            text = "Get my umbrella",
        )

        device.assertViewHasText(
            viewId = "reminderDescription",
            text = "Set location alert to never forget again",
        )

        device.assertViewHasText(
            viewId = "reminderLocationName",
            text = "Work",
        )

        device.assertViewHasText(
            viewId = "textCurrentCircularRadius",
            text = "50 meters",
        )

        device.assertViewHasText(
            viewId = "textGeofenceStatus",
            text = "Geofence is enabled",
        )
    }

    @Test
    fun g_edit_reminder_should_update_its_content_and_display_new_info_on_details_screen_and_on_the_reminder_list() {
        device.launchApp()

        device.onViewContainsTextClickAndWait(text = "Get my umbrella")

        device.onViewContainsTextClickAndWait(text = "EDIT")

        device.onViewWithIdPerformTypeText(
            viewId = "reminderLocationName",
            text = "At work",
        )

        device.onViewWithIdPerformTypeText(
            viewId = "reminderTitle",
            text = "Get my new umbrella",
        )

        device.onViewWithIdPerformTypeText(
            viewId = "reminderDescription",
            text = "Set reminder alert to never forget it again",
        )

        device.swipeUp()
        device.swipeUp()

        device.onViewWithTextClick("When I exit")

        device.onViewContainsTextClickAndWait(text = "SAVE REMINDER")

        device.assertViewHasText(
            viewId = "reminderTitle",
            text = "Get my new umbrella",
        )

        device.assertViewHasText(
            viewId = "reminderDescription",
            text = "Set reminder alert to never forget it again",
        )

        device.assertViewHasText(
            viewId = "reminderLocationName",
            text = "At work",
        )

        device.assertViewHasText(
            viewId = "textCurrentCircularRadius",
            text = "50 meters",
        )

        device.assertViewHasText(
            viewId = "textGeofenceStatus",
            text = "Geofence is enabled",
        )

        device.pressBack()

        isTextDisplayed("Get my new umbrella")
        isTextDisplayed("Set reminder alert to never forget it again")
        isTextDisplayed("At work")
    }

    @Test
    fun h_delete_reminder_should_remove_reminder_from_the_list() {
        device.launchApp()

        device.onViewWithIdClickAndWait(
            viewId = "imageDeleteReminder",
            currentAppPackage = locationReminderPackage
        )

        device.onViewWithTextClick(text = "DELETE")

        isTextNotDisplayed("Get my new umbrella")
        isTextNotDisplayed("Set reminder alert to never forget it again")
        isTextNotDisplayed("At work")
    }

    @Test
    fun i_no_reminder_content_info_should_show_when_there_is_no_reminders() {
        device.launchApp()
        isTextDisplayed("I hope you do not forget anything\nbecause you have no reminder.\n\nWhat about to add some?")
        isViewDisplayed(R.id.noDataAnimation)
    }

    @Test
    fun j_action_menu_copyrights_item_should_display_copyrights_screen() {
        device.launchApp()

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
    }

    @Test
    fun k_deleteAccount_should_display_confirmation_dialog_and_show_login_screen() {
        device.launchApp()

        val topAppBar = device.findObject(UiSelector().resourceId("$appPackage:id/topAppBar"))
        device.click(topAppBar.bounds.width() - 20, 100)

        device.onViewContainsTextClickAndWait(text = "Delete account")

        device.onViewContainsTextClickAndWait(text = "DELETE ACCOUNT")

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
