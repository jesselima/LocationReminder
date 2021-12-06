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
    private var observerState: Observer<RemindersState>? = null
    private var observerAction: Observer<RemindersAction>? = null

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = RemindersListViewModel(repository)
        observerState?.let { viewModel.state.observeForever(it) }
        observerAction?.let { viewModel.action.observeForever(it) }
    }

    @After
    fun tearDown() {
        stopKoin()
        MockitoAnnotations.openMocks(this).close()
        observerState = null
        observerAction = null
    }

    @Test
    fun viewModel_should_have_initial_state_observers_when_init() {
        assertThat(viewModel.state.value, `is`(RemindersState()))
        assertTrue(viewModel.state.hasActiveObservers())
    }

    @Test
    fun viewModel_should_have_initial_action_when_init() {
        assertEquals(viewModel.action.value, null)
    }

    @Test
    fun getReminders_should_set_reminders_list_state() = mainCoroutineRule.runBlockingTest {
        // Given
        val list = listOf(ReminderStub().reminderData, ReminderStub().reminderData2)

        whenever(repository.getReminders()).thenReturn(ResultData.Success(list))

        // When
        viewModel.getReminders()

        // Then
        val initialState = RemindersState()
        val loadingState = initialState.copy(isLoading = true)
        val finalState = loadingState.copy(
            isLoading = false,
            reminders = list.map { it.mapToPresentationModel() }
        )

        verify(observerState)?.onChanged(initialState)
        verify(observerState)?.onChanged(loadingState)
        verify(observerState)?.onChanged(finalState)
    }
}
