package com.udacity.locationreminder.features.reminderdetails

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.locationreminder.R
import com.udacity.locationreminder.common.ReminderConstants
import com.udacity.locationreminder.stubs.reminderItemView
import com.udacity.locationreminder.util.AppViewAction.performClick
import com.udacity.locationreminder.util.AppViewAction.scrollTo
import com.udacity.locationreminder.util.AppViewAssertion.isTextDisplayed
import com.udacity.locationreminder.util.AppViewAssertion.isViewDisplayed
import com.udacity.locationreminder.util.shouldNavigateTo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class ReminderDetailsFragmentTest {

    private  val navGraphOfReminderDetailsFragment = R.navigation.nav_graph_reminder_editor

    private var fragmentScenario: FragmentScenario<ReminderDetailsFragment>? = null

    @Before
    fun setup() {
        fragmentScenario = launchFragmentInContainer(
            bundleOf(
                ReminderConstants.argsKeyLastSelectedLocation to reminderItemView,
                ReminderConstants.argsKeyIsFromList to true
            ),
            R.style.LocationReminderAppTheme
        )
    }

    @Test
    fun when_screen_open_with_args_should_display_reminder_details() {
        isViewDisplayed(R.id.reminderDetailsToolbar)
        isTextDisplayed("Reminder details")
        isTextDisplayed("Barbecue stuff")
        isTextDisplayed("Meat, drinks, tomato")
        isTextDisplayed("-23.6963")
        isTextDisplayed("-46.6691")
        isTextDisplayed("50 meters")
        isTextDisplayed("Circular radius (meters) and\nalert type (enter or exit)")
        isTextDisplayed("Geofence is enabled")
        isViewDisplayed(R.id.isGeofenceEnableAnimation)
        scrollTo(R.id.buttonDeleteReminderAndGeofence)
        isTextDisplayed("Edit")
        isTextDisplayed("Delete")
    }

    @Test
    fun onDeleteReminderClicked_should_display_delete_dialog() {
        isTextDisplayed("DELETE")
        scrollTo(R.id.buttonDeleteReminderAndGeofence)
        performClick(R.id.buttonDeleteReminderAndGeofence)
        isTextDisplayed("Delete reminder?")
        isTextDisplayed("The local reminder data and geofence trigger will be removed.")
        isTextDisplayed("Delete")
        isTextDisplayed("Cancel")
    }

    @Test
    fun onEditReminderClicked_should_navigate_to_edit_reminder_screen() {
        scrollTo(R.id.buttonEditReminderAndGeofence)

        fragmentScenario?.shouldNavigateTo(
            onClickedViewWithResId = R.id.buttonEditReminderAndGeofence,
            destinationResId = R.id.saveReminderFragment,
            navGraph = navGraphOfReminderDetailsFragment
        )
    }
}