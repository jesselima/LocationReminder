package com.udacity.locationreminder.features.addremindder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.udacity.locationreminder.MainCoroutineRule
import com.udacity.locationreminder.R
import com.udacity.locationreminder.core.di.MainModule
import com.udacity.locationreminder.features.addreminder.presentation.AddReminderFragment
import com.udacity.locationreminder.features.addreminder.presentation.AddReminderViewModel
import com.udacity.locationreminder.features.addreminder.usecase.InputValidatorsUseCase
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.locationreminder.sharedpresentation.ReminderItemView
import com.udacity.locationreminder.sharedpresentation.mapToDataModel
import com.udacity.locationreminder.stubs.reminderData1
import com.udacity.locationreminder.stubs.reminderItemView
import com.udacity.locationreminder.util.AppViewAction.actionSwipeUp
import com.udacity.locationreminder.util.AppViewAction.performClick
import com.udacity.locationreminder.util.AppViewAction.performType
import com.udacity.locationreminder.util.AppViewAssertion.assertSlider
import com.udacity.locationreminder.util.AppViewAssertion.isViewIdWithTextDisplayed
import com.udacity.locationreminder.util.AppViewAssertion.setSliderToValue
import com.udacity.locationreminder.util.shouldNavigateTo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class AddReminderFragmentTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val navGraphOfMain = R.navigation.nav_graph_main
    private val navGraphOfEditor = R.navigation.nav_graph_reminder_editor
    private val repository: RemindersLocalRepository = mock()
    private val inputValidatorsUseCase = InputValidatorsUseCase()

    @Before
    fun init() {
        runBlocking {
            stopKoin()

            startKoin {
                androidContext(InstrumentationRegistry.getInstrumentation().context)
                MainModule().initMainModules()
            }

            val module = module {
                viewModel(override = true) {
                    AddReminderViewModel(
                        remindersLocalRepository = repository,
                        inputValidatorsUseCase = inputValidatorsUseCase
                    )
                }
            }
            loadKoinModules(module)
        }
    }

    @Test
    fun add_reminder_with_success_should_navigate_to_reminders_list() = mainCoroutineRule.runBlockingTest {
        whenever(repository.saveReminder(reminderData1)).thenReturn(999)

        val fragmentScenario = launchFragmentInContainer<AddReminderFragment>(
            fragmentArgs = bundleOf(
                "lastSelectedLocation" to ReminderItemView(
                    latitude = -90.0,
                    longitude = -210.0,
                ),
                "isFromList" to false
            ),
            themeResId = R.style.LocationReminderAppTheme
        )

        performType(R.id.reminderLocationName, reminderData1.locationName)
        performType(R.id.reminderTitle, reminderData1.title)
        performType(R.id.reminderDescription, reminderData1.description)

        actionSwipeUp(R.id.scrollContentLayout)

        performClick(R.id.radioButtonExit)
        performType(R.id.expirationDurationEditText, reminderData1.expiration.toString())
        performClick(R.id.isGeofenceEnableSwitch)

        reminderData1.circularRadius?.let { setSliderToValue(R.id.sliderCircularRadius, it) }

        performClick(R.id.actionButtonSaveReminder)

        fragmentScenario.shouldNavigateTo(
            onClickedViewWithResId = R.id.actionButtonSaveReminder,
            destinationResId = R.id.reminderListFragment,
            navGraph = navGraphOfMain
        )
    }

    @Test
    fun edit_reminder_should_autofill_inputs_when_editing_and_navigate_to_details() = mainCoroutineRule.runBlockingTest {
        whenever(repository.updateReminder(reminderData1)).thenReturn(1)

        val fragmentScenario = launchFragmentInContainer<AddReminderFragment>(
            fragmentArgs = bundleOf(
                "lastSelectedLocation" to reminderItemView,
                "isFromList" to true,
                "isEditing" to true
            ),
            themeResId = R.style.LocationReminderAppTheme
        )

        isViewIdWithTextDisplayed(R.id.reminderLocationName, reminderItemView.locationName.toString())
        isViewIdWithTextDisplayed(R.id.reminderTitle, reminderItemView.title.toString())
        isViewIdWithTextDisplayed(R.id.reminderDescription, reminderItemView.description.toString())

        actionSwipeUp(R.id.scrollContentLayout)

        assertSlider(R.id.sliderCircularRadius, 50.0f, 450.0f)
        performClick(R.id.radioButtonExit)
        performClick(R.id.isGeofenceEnableSwitch)

        fragmentScenario.shouldNavigateTo(
            onClickedViewWithResId = R.id.actionButtonSaveReminder,
            destinationResId = R.id.reminderDetailsFragment,
            navGraph = navGraphOfEditor
        )

        val updatedReminderItemView = reminderItemView.copy(
            circularRadius = 450.0f,
            transitionType = 2
        )

        verify(repository).updateReminder(updatedReminderItemView.mapToDataModel())
    }

}