package com.udacity.project4.features.reminderdetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.common.ReminderConstants
import com.udacity.project4.core.di.MainModule
import com.udacity.project4.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.project4.stubs.reminderItemView
import com.udacity.project4.util.AppViewAction.performClick
import com.udacity.project4.util.AppViewAction.scrollToViewWithId
import com.udacity.project4.util.AppViewAssertion.isTextDisplayed
import com.udacity.project4.util.AppViewAssertion.isViewDisplayed
import com.udacity.project4.util.shouldNavigateTo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class ReminderDetailsFragmentTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val repository: RemindersLocalRepository = mock()
    private val navGraphOfReminderDetailsFragment = R.navigation.nav_graph_reminder_editor

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        runBlocking {
            stopKoin()

            startKoin {
                androidContext(InstrumentationRegistry.getInstrumentation().context)
                MainModule().initMainModules()
            }

            val module = module {
                factory (override = true) { repository }
            }

            loadKoinModules(module)
        }
    }

    @After
    fun clear() {
        MockitoAnnotations.openMocks(this).close()
    }

    @Test
    fun when_screen_open_with_args_should_display_reminder_details() {
        launchFragmentInContainer<ReminderDetailsFragment>(
            bundleOf(
                ReminderConstants.argsKeyLastSelectedLocation to reminderItemView,
                ReminderConstants.argsKeyIsFromList to true,
                ReminderConstants.argsKeyIsEditing to false
            ),
            R.style.LocationReminderAppTheme
        )

        isViewDisplayed(R.id.reminderDetailsToolbar)
        isTextDisplayed("Reminder details")
        isTextDisplayed("Barbecue stuff")
        isTextDisplayed("Meat, drinks, tomato")
        isTextDisplayed("-23.6963")
        isTextDisplayed("-46.6691")
        isTextDisplayed("450 meters")
        isTextDisplayed("Circular radius (meters) and\nalert type (enter or exit)")
        isTextDisplayed("Geofence is disabled")
        isViewDisplayed(R.id.imageReminderGeofenceStatusDisabled)
        scrollToViewWithId(R.id.buttonDeleteReminderAndGeofence)
        isTextDisplayed("Edit")
        isTextDisplayed("Delete")
    }

    @Test
    fun onDeleteReminderClicked_should_display_delete_dialog() = mainCoroutineRule.runBlockingTest {
        whenever(repository.deleteReminder(any())).thenReturn(1)

        launchFragmentInContainer<ReminderDetailsFragment>(
            bundleOf(
                ReminderConstants.argsKeyLastSelectedLocation to reminderItemView,
                ReminderConstants.argsKeyIsFromList to true,
                ReminderConstants.argsKeyIsEditing to true
            ),
            R.style.LocationReminderAppTheme
        )

        isTextDisplayed("DELETE")
        scrollToViewWithId(R.id.buttonDeleteReminderAndGeofence)
        performClick(R.id.buttonDeleteReminderAndGeofence)
        isTextDisplayed("Delete reminder?")
        isTextDisplayed("The local reminder data and geofence trigger will be removed.")
        isTextDisplayed("Delete")
        isTextDisplayed("Cancel")
    }

    @Test
    fun onEditReminderClicked_should_navigate_to_edit_reminder_screen() {
        val fragmentScenario = launchFragmentInContainer<ReminderDetailsFragment>(
            bundleOf(
                ReminderConstants.argsKeyLastSelectedLocation to reminderItemView,
                ReminderConstants.argsKeyIsFromList to true,
                ReminderConstants.argsKeyIsEditing to true
            ),
            R.style.LocationReminderAppTheme
        )

        scrollToViewWithId(R.id.buttonEditReminderAndGeofence)

        fragmentScenario.shouldNavigateTo(
            onClickedViewWithResId = R.id.buttonEditReminderAndGeofence,
            destinationResId = R.id.saveReminderFragment,
            navGraph = navGraphOfReminderDetailsFragment
        )
    }
}