package com.udacity.project4.features.addreminder.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.features.addreminder.domain.usecase.InputValidatorsUseCase
import com.udacity.project4.shareddata.localdatasource.models.ResultData
import com.udacity.project4.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.project4.shareddata.stub.ReminderStub
import com.udacity.project4.sharedpresentation.mapToDataModel
import com.udacity.project4.tools.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AddReminderViewModelTest {

    /** Executes each task synchronously using Architecture Components. */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /** Set the main coroutines dispatcher for unit testing. */
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val repository: RemindersLocalRepository = mock()
    private val useCase: InputValidatorsUseCase = mock()
    private lateinit var viewModel: AddReminderViewModel

    @Mock
    private var observerState: Observer<AddReminderState> = Observer { AddReminderState() }

    @Mock
    private var observerAction: Observer<AddReminderAction> = Observer {  }

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = AddReminderViewModel(repository, useCase)
        viewModel.state.observeForever(observerState)
        viewModel.action.observeForever(observerAction)
    }

    @After
    fun tearDown() {
        stopKoin()
        MockitoAnnotations.openMocks(this).close()
    }

    @Test
    fun viewModel_should_have_active_state_observers_after_init() {
        MatcherAssert.assertThat(viewModel.state.value, Is.`is`(AddReminderState()))
        Assert.assertTrue(viewModel.state.hasActiveObservers())
    }

    @Test
    fun viewModel_should_have_active_action_observers_after_init() {
        Assert.assertEquals(viewModel.action.value, AddReminderAction.ClearErrors)
        Assert.assertTrue(viewModel.action.hasActiveObservers())
    }

    @Test
    fun isTitleValid_should_set_InputErrorFieldTitle_when_title_is_empty() {
        // When
        viewModel.isTitleValid("")

        // Then
        verify(observerAction).onChanged(AddReminderAction.InputErrorFieldTitle)
    }

    @Test
    fun isTitleValid_should_set_InputErrorFieldTitle_when_title_is_null() {
        // When
        viewModel.isTitleValid(null)

        // Then
        verify(observerAction).onChanged(AddReminderAction.InputErrorFieldTitle)
    }

    @Test
    fun isTitleValid_should_do_nothing_when_title_is_valid() {
        // Given
        val initialState = AddReminderState()
        val titleState = initialState.copy(selectedReminder = null)

        // When
        viewModel.isTitleValid("Home")

        // Then
        verify(observerState).onChanged(initialState)
        verify(observerState).onChanged(titleState)
    }

    @Test
    fun isLocationNameValid_should_set_InputErrorFieldTitle_when_title_is_empty() {
        // When
        viewModel.isLocationNameValid("")

        // Then
        verify(observerAction).onChanged(AddReminderAction.InputErrorFieldLocationName)
    }

    @Test
    fun isLocationNameValid_should_set_InputErrorFieldTitle_when_title_is_null() {
        // When
        viewModel.isLocationNameValid(null)

        // Then
        verify(observerAction).onChanged(AddReminderAction.InputErrorFieldLocationName)
    }

    @Test
    fun isLocationNameValid_should_do_nothing_when_title_is_valid() {
        // Given
        val initialState = AddReminderState()
        val titleState = initialState.copy(selectedReminder = null)

        // When
        viewModel.isLocationNameValid("Sao Paulo")

        // Then
        verify(observerState).onChanged(initialState)
        verify(observerState).onChanged(titleState)
    }

    @Test
    fun isDescriptionValid_should_set_InputErrorFieldTitle_when_title_is_empty() {
        // When
        viewModel.isDescriptionValid("")

        // Then
        verify(observerAction).onChanged(AddReminderAction.InputErrorFieldDescription)
    }

    @Test
    fun isDescriptionValid_should_set_InputErrorFieldTitle_when_title_is_null() {
        // When
        viewModel.isDescriptionValid(null)

        // Then
        verify(observerAction).onChanged(AddReminderAction.InputErrorFieldDescription)
    }

    @Test
    fun isDescriptionValid_should_do_nothing_when_title_is_valid() {
        // Given
        val initialState = AddReminderState()
        val titleState = initialState.copy(selectedReminder = null)

        // When
        viewModel.isDescriptionValid("Get all camping equipment")

        // Then
        verify(observerState).onChanged(initialState)
        verify(observerState).onChanged(titleState)
    }

    @Test
    fun setSelectedReminder_should_set_reminder_state_when_reminder_is_valid() {
        // When
        viewModel.setSelectedReminder(ReminderStub().reminderItemView)

        // Then
        verify(observerState).onChanged(AddReminderState(selectedReminder = ReminderStub().reminderItemView))
    }

    @Test
    fun setSelectedReminder_should_set_reminder_initial_state_when_reminder_is_null() {
        // When
        viewModel.setSelectedReminder(null)

        // Then
        verify(observerState).onChanged(AddReminderState())
    }

    @Test
    fun isLatLngValid_should_set_InputErrorMissingLatLong_when_latitude_and_longitude_are_null() {
        // Given
        viewModel.setSelectedReminder(ReminderStub().reminderItemView.copy(
            latitude = null,
            longitude = null
        ))

        // When
        viewModel.isLatLngValid()

        // Then
        verify(observerAction).onChanged(AddReminderAction.InputErrorMissingLatLong)
    }

    @Test
    fun isLatLngValid_should_do_nothing_when_latitude_and_longitude_are_valid() {
        // Given
        viewModel.setSelectedReminder(ReminderStub().reminderItemView)

        // When
        viewModel.isLatLngValid()

        // Then
        verify(observerState).onChanged(AddReminderState())
        verify(observerState).onChanged(AddReminderState(selectedReminder = ReminderStub().reminderItemView))
    }

    @Test
    fun saveReminder_should_set_FormIsValid_when_form_is_valid_and_save_is_success()
    = mainCoroutineRule.runBlockingTest {
        // Given
        viewModel.setSelectedReminder(ReminderStub().reminderItemView)

        whenever(repository.saveReminder(ReminderStub().reminderItemView.mapToDataModel()))
            .thenReturn(ResultData.Success(1))
        whenever(useCase.isTitleValid(any())).thenReturn(true)
        whenever(useCase.isLocationNameValid(any())).thenReturn(true)
        whenever(useCase.isDescriptionValid(any())).thenReturn(true)

        // When
        viewModel.validateFieldsAndSaveOrUpdateReminder(isEditing = false)

        verify(observerAction).onChanged(AddReminderAction.FormIsValid)
    }

    @Test
    fun saveReminder_should_set_ClearErrors_and_AddReminderSuccess_actions_when_form_is_valid_isGeofenceEnable_is_false_and_save_is_success()
            = mainCoroutineRule.runBlockingTest {
        // Given
        viewModel.setSelectedReminder(ReminderStub().reminderItemView.copy(isGeofenceEnable = false))

        whenever(repository.saveReminder(ReminderStub().reminderItemView.mapToDataModel().copy(isGeofenceEnable = false)))
            .thenReturn(ResultData.Success(1))
        whenever(useCase.isTitleValid(any())).thenReturn(true)
        whenever(useCase.isLocationNameValid(any())).thenReturn(true)
        whenever(useCase.isDescriptionValid(any())).thenReturn(true)

        // When
        viewModel.validateFieldsAndSaveOrUpdateReminder(isEditing = false)

        verify(observerAction).onChanged(AddReminderAction.AddReminderSuccess(id = 1))
        verify(observerAction, times(3)).onChanged(AddReminderAction.ClearErrors)
    }

    @Test
    fun saveReminder_should_set_FormIsValid_action_when_form_is_valid_and_save_is_error()
    = mainCoroutineRule.runBlockingTest {
        // Given
        viewModel.setSelectedReminder(ReminderStub().reminderItemView)

        whenever(repository.saveReminder(ReminderStub().reminderItemView.mapToDataModel()))
            .thenReturn(ResultData.Error(message = "Error saving reminder", statusCode = -1))
        whenever(useCase.isTitleValid(any())).thenReturn(true)
        whenever(useCase.isLocationNameValid(any())).thenReturn(true)
        whenever(useCase.isDescriptionValid(any())).thenReturn(true)

        // When
        viewModel.validateFieldsAndSaveOrUpdateReminder(isEditing = false)

        verify(observerAction).onChanged(AddReminderAction.FormIsValid)
    }

    @Test
    fun saveReminder_should_set_AddReminderError_action_when_shouldSaveWithoutGeofence_is_true_and_form_is_valid_and_save_is_error()
    = mainCoroutineRule.runBlockingTest {
        // Given
        viewModel.setSelectedReminder(ReminderStub().reminderItemView)

        whenever(repository.saveReminder(ReminderStub().reminderItemView.mapToDataModel()))
            .thenReturn(ResultData.Error(message = "Error saving reminder", statusCode = -1))
        whenever(useCase.isTitleValid(any())).thenReturn(true)
        whenever(useCase.isLocationNameValid(any())).thenReturn(true)
        whenever(useCase.isDescriptionValid(any())).thenReturn(true)

        // When
        viewModel.validateFieldsAndSaveOrUpdateReminder(isEditing = false, shouldSaveWithoutGeofence = true)

        verify(observerAction).onChanged(AddReminderAction.AddReminderError)
    }

    @Test
    fun updateReminder_should_set_FormIsValid_when_form_is_valid_and_updated_is_success()
    = mainCoroutineRule.runBlockingTest {
        // Given
        viewModel.setSelectedReminder(ReminderStub().reminderItemView)

        whenever(repository.updateReminder(ReminderStub().reminderItemView.mapToDataModel()))
            .thenReturn(ResultData.Success(1))
        whenever(useCase.isTitleValid(any())).thenReturn(true)
        whenever(useCase.isLocationNameValid(any())).thenReturn(true)
        whenever(useCase.isDescriptionValid(any())).thenReturn(true)

        // When
        viewModel.validateFieldsAndSaveOrUpdateReminder(isEditing = false)

        verify(observerAction).onChanged(AddReminderAction.FormIsValid)
    }

    @Test
    fun updateReminder_should_set_FormIsValid_when_form_is_valid_and_update_is_error()
    = mainCoroutineRule.runBlockingTest {
        // Given
        viewModel.setSelectedReminder(ReminderStub().reminderItemView)

        whenever(repository.updateReminder(ReminderStub().reminderItemView.mapToDataModel()))
            .thenReturn(ResultData.Error("UpdateReminder Error!", -1))
        whenever(useCase.isTitleValid(any())).thenReturn(true)
        whenever(useCase.isLocationNameValid(any())).thenReturn(true)
        whenever(useCase.isDescriptionValid(any())).thenReturn(true)

        // When
        viewModel.validateFieldsAndSaveOrUpdateReminder(isEditing = false)

        verify(observerAction).onChanged(AddReminderAction.FormIsValid)
    }
}