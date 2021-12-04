package com.udacity.locationreminder.features.onboarding

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.locationreminder.shareddata.stub.Steps
import com.udacity.locationreminder.shareddata.getOrAwaitValue
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class OnBoardingViewModelTest: TestCase() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun setOnBoardingStep_should_send_onboarding_step_state() {
        val onBoardingViewModel = OnBoardingViewModel()
        onBoardingViewModel.setOnBoardingStep(0)
        val result = onBoardingViewModel.onBoardingStepLiveData.getOrAwaitValue().peekContent()
        assertThat(result, `is`(Steps.onBoardingSteps[0]))
    }
}


