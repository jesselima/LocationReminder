package com.udacity.project4.features.onboarding

import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.util.AppViewAction
import com.udacity.project4.util.AppViewAssertion
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class ActivityOnBoardingTest {

    @Test
    fun launchOnBoardingActivity_should_pass_throughout_all_steps_then_launch_reminders_screen() {
        launchActivity<OnBoardingActivity>()

        AppViewAssertion.isViewDisplayed(R.id.onBoardingViewPager)
        AppViewAssertion.isViewDisplayed(R.id.tabLayout)

        AppViewAction.actionSwipeLeft(R.id.onBoardingStepImage)
        AppViewAction.actionSwipeLeft(R.id.onBoardingStepImage)

        AppViewAssertion.isViewDisplayed(R.id.onboardingButtonStart)
        AppViewAssertion.isTextDisplayed("Start")
    }
}
