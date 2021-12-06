package com.udacity.locationreminder.features.addreminder.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.locationreminder.features.addreminder.usecase.InputValidatorsUseCase
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.locationreminder.tools.MainCoroutineRule

import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        Assert.assertEquals(viewModel.action.value, null)
        Assert.assertTrue(viewModel.action.hasActiveObservers())
    }
}