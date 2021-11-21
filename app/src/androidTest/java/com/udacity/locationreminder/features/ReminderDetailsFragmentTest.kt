package com.udacity.locationreminder.features

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.locationreminder.R
import com.udacity.locationreminder.common.ReminderConstants
import com.udacity.locationreminder.features.reminderdetails.ReminderDetailsFragment
import com.udacity.locationreminder.stubs.reminderItemView
import com.udacity.locationreminder.util.AppViewAction
import com.udacity.locationreminder.util.AppViewAssertion
import com.udacity.locationreminder.util.shouldNavigateTo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class ReminderDetailsFragmentTest {

    private  val navGraphOfReminderDetailsFragment = R.navigation.nav_graph_reminder_editor

    @Test
    fun when_screen_open_with_args_should_display_reminder_details() {

        launchFragmentInContainer<ReminderDetailsFragment>(
            bundleOf(
                ReminderConstants.argsKeyLastSelectedLocation to reminderItemView,
                ReminderConstants.argsKeyIsFromList to true
            ),
            R.style.LocationReminderAppTheme
        )

        AppViewAssertion.isDisplayed(R.id.reminderDetailsToolbar)
        AppViewAssertion.isTextDisplayed("Reminder details")
        AppViewAssertion.isTextDisplayed("Barbecue stuff")
        AppViewAssertion.isTextDisplayed("Meat, drinks, tomato")
        AppViewAssertion.isTextDisplayed("-23.6963")
        AppViewAssertion.isTextDisplayed("-46.6691")
        AppViewAssertion.isTextDisplayed("50 meters")
        AppViewAssertion.isTextDisplayed("Circular radius (meters) and\nalert type (enter or exit)")
        AppViewAssertion.isTextDisplayed("Geofence is enabled")
        AppViewAssertion.isDisplayed(R.id.isGeofenceEnableAnimation)
        AppViewAction.scrollTo(R.id.buttonDeleteReminderAndGeofence)
        AppViewAssertion.isTextDisplayed("Edit")
        AppViewAssertion.isTextDisplayed("Delete")
    }

    @Test
    fun onDeleteReminderClicked_should_display_delete_dialog() {

        launchFragmentInContainer<ReminderDetailsFragment>(
            bundleOf(
                ReminderConstants.argsKeyLastSelectedLocation to reminderItemView,
                ReminderConstants.argsKeyIsFromList to true
            ),
            R.style.LocationReminderAppTheme
        )

        AppViewAssertion.isTextDisplayed("DELETE")
        AppViewAction.scrollTo(R.id.buttonDeleteReminderAndGeofence)
        AppViewAction.performClick(R.id.buttonDeleteReminderAndGeofence)
        AppViewAssertion.isTextDisplayed("Delete reminder?")
        AppViewAssertion.isTextDisplayed("The local reminder data and geofence trigger will be removed.")
        AppViewAssertion.isTextDisplayed("Delete")
        AppViewAssertion.isTextDisplayed("Cancel")
    }

    @Test
    fun onEditReminderClicked_should_navigate_to_edit_reminder_screen() {
        // Given
        val fragmentScenario = launchFragmentInContainer<ReminderDetailsFragment>(
            bundleOf(
                ReminderConstants.argsKeyLastSelectedLocation to reminderItemView,
                ReminderConstants.argsKeyIsFromList to true
            ),
            R.style.LocationReminderAppTheme
        )

        // When
        AppViewAction.scrollTo(R.id.buttonEditReminderAndGeofence)

        // Then
        fragmentScenario.shouldNavigateTo(
            onClickedViewWithResId = R.id.buttonEditReminderAndGeofence,
            destinationResId = R.id.saveReminderFragment,
            navGraph = navGraphOfReminderDetailsFragment
        )
    }
}