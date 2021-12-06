package com.udacity.locationreminder.features.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.locationreminder.shareddata.localdatasource.models.ResultData
import com.udacity.locationreminder.shareddata.localdatasource.models.mapToPresentationModel
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.locationreminder.shareddata.stub.ReminderStub
import com.udacity.locationreminder.tools.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    /** Executes each task synchronously using Architecture Components. */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /** Set the main coroutines dispatcher for unit testing. */
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val repository: RemindersLocalRepository = mock()
    private lateinit var viewModel: RemindersListViewModel

    @Mock
    private var observerState: Observer<RemindersState> = Observer { RemindersState() }

    @Mock
    private var observerAction: Observer<RemindersAction> = Observer {  }

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = RemindersListViewModel(repository)
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
        assertThat(viewModel.state.value, `is`(RemindersState()))
        assertTrue(viewModel.state.hasActiveObservers())
    }

    @Test
    fun viewModel_should_have_active_action_observers_after_init() {
        assertEquals(viewModel.action.value, null)
        assertTrue(viewModel.action.hasActiveObservers())
    }

    @Test
    fun getReminders_should_set_reminders_list_state() = mainCoroutineRule.runBlockingTest {
        // Given
        val list = listOf(ReminderStub().reminderData, ReminderStub().reminderData2)

        whenever(repository.getReminders()).thenReturn(ResultData.Success(list))

        val initialState = RemindersState()
        val loadingState = initialState.copy(isLoading = true)
        val finalState = loadingState.copy(
            isLoading = false,
            reminders = list.map { it.mapToPresentationModel() }
        )

        // When
        viewModel.getReminders()

        // Then
        verify(observerState).onChanged(initialState)
        verify(observerState).onChanged(loadingState)
        verify(observerState).onChanged(finalState)
    }

    @Test
    fun getReminders_should_set_NoRemindersFound_action_when_no_reminders_was_found() = mainCoroutineRule.runBlockingTest {
        // Given
        whenever(repository.getReminders()).thenReturn(ResultData.Success(emptyList()))
        val initialState = RemindersState()
        val loadingState = initialState.copy(isLoading = true)
        val finalState = loadingState.copy(isLoading = false, reminders = emptyList())

        // When
        viewModel.getReminders()

        // Then
        verify(observerState).onChanged(initialState)
        verify(observerState).onChanged(loadingState)
        verify(observerState).onChanged(finalState)
        verify(observerAction).onChanged(RemindersAction.NoRemindersFound)
    }

    @Test
    fun getReminders_should_set_LoadRemindersError_action_when_no_reminders_was_found() = mainCoroutineRule.runBlockingTest {
        // Given
        whenever(repository.getReminders()).thenReturn(ResultData.Error("Ooops!"))
        val initialState = RemindersState()
        val loadingState = initialState.copy(isLoading = true)
        val finalState = loadingState.copy(isLoading = false, reminders = null)

        // When
        viewModel.getReminders()

        // Then
        verify(observerState, times(2)).onChanged(initialState)
        verify(observerState).onChanged(loadingState)
        verify(observerState, times(2)).onChanged(finalState)
        verify(observerAction).onChanged(RemindersAction.LoadRemindersError)
    }

    @Test
    fun updateGeofenceStatus_should_set_UpdateRemindersSuccess_action_when_update_is_success() = mainCoroutineRule.runBlockingTest {
        // Given
        /** The return value is stands for the number of row update on the database */
        whenever(repository.updateGeofenceStatus(202112060802, true))
            .thenReturn(1)

        // When
        viewModel.updateGeofenceStatus(202112060802, true)

        // Then
        verify(observerAction).onChanged(RemindersAction.UpdateRemindersSuccess)
    }

    @Test
    fun updateGeofenceStatus_should_set_UpdateRemindersError_action_when_update_is_error() = mainCoroutineRule.runBlockingTest {
        // Given
        whenever(repository.updateGeofenceStatus(202112060802, true))
            .thenReturn(0)

        // When
        viewModel.updateGeofenceStatus(202112060802, true)

        // Then
        verify(observerAction).onChanged(RemindersAction.UpdateRemindersError)
    }

    @Test
    fun deleteAllReminders_should_set_UpdateRemindersSuccess_action_when_delete_is_success() = mainCoroutineRule.runBlockingTest {
        // Given
        /** The return value is stands for the number of row deleted from the database */
        whenever(repository.deleteAllReminders())
            .thenReturn(3)

        // When
        viewModel.deleteAllReminders()

        // Then
        verify(observerAction).onChanged(RemindersAction.DeleteAllRemindersSuccess)
    }

    @Test
    fun deleteAllReminders_should_set_UpdateRemindersError_action_when_delete_is_error() = mainCoroutineRule.runBlockingTest {
        // Given
        /** The return value is stands for the number of row deleted from the database */
        whenever(repository.deleteAllReminders())
            .thenReturn(0)

        // When
        viewModel.deleteAllReminders()

        // Then
        verify(observerAction).onChanged(RemindersAction.DeleteAllRemindersError)
    }
}
