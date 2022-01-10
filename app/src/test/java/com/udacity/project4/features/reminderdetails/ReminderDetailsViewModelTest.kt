package com.udacity.project4.features.reminderdetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.features.reminderslist.RemindersState
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class ReminderDetailsViewModelTest {

    /** Executes each task synchronously using Architecture Components. */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /** Set the main coroutines dispatcher for unit testing. */
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val repository: RemindersLocalRepository = mock()
    private lateinit var viewModel: ReminderDetailsViewModel

    @Mock
    private var observerState: Observer<ReminderDetailsState> = Observer { RemindersState() }

    @Mock
    private var observerAction: Observer<ReminderDetailsAction> = Observer {  }

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = ReminderDetailsViewModel(repository)
        viewModel.state.observeForever(observerState)
        viewModel.action.observeForever(observerAction)
    }

    @After
    fun tearDown() {
        stopKoin()
        observerState =  Observer { ReminderDetailsState() }
        observerAction = Observer { }
        MockitoAnnotations.openMocks(this).close()
    }

    @Test
    fun viewModel_should_have_active_state_observers_after_init() {
        MatcherAssert.assertThat(viewModel.state.value, Is.`is`(ReminderDetailsState()))
        Assert.assertTrue(viewModel.state.hasActiveObservers())
    }

    @Test
    fun viewModel_should_have_active_action_observers_after_init() {
        Assert.assertEquals(viewModel.action.value, null)
        Assert.assertTrue(viewModel.action.hasActiveObservers())
    }

    @Test
    fun deleteReminder_should_set_DeleteReminderSuccess_when_delete_is_success() = mainCoroutineRule.runBlockingTest {
        // Given
        whenever(repository.deleteReminder(ReminderStub().reminderItemView.mapToDataModel()))
            .thenReturn(ResultData.Success(1))

        val initialState = ReminderDetailsState()
        val loadingState = initialState.copy(isLoading = true)
        val finalState = loadingState.copy(isLoading = false)

        // When
        viewModel.deleteReminder(ReminderStub().reminderItemView)

        // Then
        verify(observerState, times(2)).onChanged(initialState)
        verify(observerState).onChanged(loadingState)
        verify(observerState, times(2)).onChanged(finalState)
        verify(observerAction).onChanged(ReminderDetailsAction.DeleteReminderSuccess)
    }

    @Test
    fun deleteReminder_should_set_DeleteReminderError_when_delete_is_error() = mainCoroutineRule.runBlockingTest {
        // Given
        whenever(repository.deleteReminder(ReminderStub().reminderItemView.mapToDataModel()))
            .thenReturn(ResultData.Error(message = "Delete Reminder Error!", statusCode = -1))

        val initialState = ReminderDetailsState()
        val loadingState = initialState.copy(isLoading = true)
        val finalState = loadingState.copy(isLoading = false)

        // When
        viewModel.deleteReminder(ReminderStub().reminderItemView)

        // Then
        verify(observerState, times(2)).onChanged(initialState)
        verify(observerState).onChanged(loadingState)
        verify(observerState, times(2)).onChanged(finalState)
        verify(observerAction).onChanged(ReminderDetailsAction.DeleteReminderError)
    }
}