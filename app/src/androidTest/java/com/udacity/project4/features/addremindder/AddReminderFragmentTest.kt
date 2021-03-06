package com.udacity.project4.features.addremindder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.core.di.MainModule
import com.udacity.project4.features.addreminder.domain.usecase.InputValidatorsUseCase
import com.udacity.project4.features.addreminder.presentation.AddReminderFragment
import com.udacity.project4.features.addreminder.presentation.AddReminderViewModel
import com.udacity.project4.shareddata.localdatasource.models.ResultData
import com.udacity.project4.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.project4.sharedpresentation.ReminderItemView
import com.udacity.project4.sharedpresentation.mapToDataModel
import com.udacity.project4.stubs.reminderData1
import com.udacity.project4.stubs.reminderItemView
import com.udacity.project4.util.AppViewAction.actionSwipeUp
import com.udacity.project4.util.AppViewAction.performClick
import com.udacity.project4.util.AppViewAction.performType
import com.udacity.project4.util.AppViewAssertion.assertSlider
import com.udacity.project4.util.AppViewAssertion.isTextDisplayed
import com.udacity.project4.util.AppViewAssertion.isViewIdWithTextDisplayed
import com.udacity.project4.util.AppViewAssertion.setSliderToValue
import com.udacity.project4.util.shouldNavigateTo
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
    private val remindersLocalRepository: RemindersLocalRepository = mock()
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
                viewModel {
                    AddReminderViewModel(
                        remindersLocalRepository = remindersLocalRepository,
                        inputValidatorsUseCase = inputValidatorsUseCase
                    )
                }
            }
            loadKoinModules(module)
        }
    }

    @Test
    fun add_reminder_with_success_should_navigate_to_reminders_list() = mainCoroutineRule.runBlockingTest {
        whenever(remindersLocalRepository.saveReminder(reminderData1)).thenReturn(ResultData.Success(999))

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

        fragmentScenario.shouldNavigateTo(
            onClickedViewWithResId = R.id.actionButtonSaveReminder,
            destinationResId = R.id.reminderListFragment,
            navGraph = navGraphOfMain
        )
    }

    @Test
    fun add_reminder_with_error_should_display_error_content() = mainCoroutineRule.runBlockingTest {
        whenever(remindersLocalRepository.saveReminder(reminderData1)).thenReturn(ResultData.Error(""))

        launchFragmentInContainer<AddReminderFragment>(
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

        isTextDisplayed("Ooops! Error saving reminder.")
        isTextDisplayed("Dismiss")
    }

    @Test
    fun edit_reminder_should_updateReminder_when_inputs_are_valid() = mainCoroutineRule.runBlockingTest {
        val updateResultSuccess = 1
        whenever(remindersLocalRepository.updateReminder(
            reminderItemView.copy(circularRadius = 450.0f).mapToDataModel())
        ).thenReturn(ResultData.Success(updateResultSuccess))


        launchFragmentInContainer<AddReminderFragment>(
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

        performClick(R.id.actionButtonSaveReminder)

        verify(remindersLocalRepository).updateReminder(
            reminderItemView.copy(circularRadius = 450.0f, isGeofenceEnable = false).mapToDataModel()
        )
    }
}