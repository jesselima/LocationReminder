package com.udacity.locationreminder.features.onboarding

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.locationreminder.R
import com.udacity.locationreminder.stubs.Steps
import com.udacity.locationreminder.util.AppViewAction
import com.udacity.locationreminder.util.AppViewAssertion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class OnBoardingStepFragmentTest {

    @Test
    fun when_screen_open_should_display_onboarding_first_step() {
        launchFragmentInContainer<OnBoardingStepFragment>(
            fragmentArgs = bundleOf(
                "section_number" to 0,
                "intro_step" to Steps.onBoardingSteps[0]
            ),
            themeResId = R.style.LocationReminderAppTheme
        )
        AppViewAssertion.isDisplayed(R.id.onBoardingStepImage)
        AppViewAssertion.isDisplayed(R.id.onBoardingStepTitle)
        AppViewAssertion.isDisplayed(R.id.onBoardingStepDescription)
        AppViewAssertion.isTextDisplayed("Need remember something?")
        AppViewAssertion.isTextDisplayed("Have you forgot to do something while you was in some place?")
        AppViewAction.actionSwipeLeft(R.id.onBoardingStepImage)
    }

    @Test
    fun when_screen_open_should_display_onboarding_second_step() {
        launchFragmentInContainer<OnBoardingStepFragment>(
            fragmentArgs = bundleOf(
                "section_number" to 1,
                "intro_step" to Steps.onBoardingSteps[1]
            ),
            themeResId = R.style.LocationReminderAppTheme
        )
        AppViewAssertion.isDisplayed(R.id.onBoardingStepImage)
        AppViewAssertion.isDisplayed(R.id.onBoardingStepTitle)
        AppViewAssertion.isDisplayed(R.id.onBoardingStepDescription)
        AppViewAssertion.isTextDisplayed("No worries!")
        AppViewAssertion.isTextDisplayed("Here you can select a location and save your reminder.")
        AppViewAction.actionSwipeLeft(R.id.onBoardingStepImage)
    }

    @Test
    fun when_screen_open_should_display_onboarding_third_step() {
        launchFragmentInContainer<OnBoardingStepFragment>(
            fragmentArgs = bundleOf(
                "section_number" to 2,
                "intro_step" to Steps.onBoardingSteps[2]
            ),
            themeResId = R.style.LocationReminderAppTheme
        )
        AppViewAssertion.isDisplayed(R.id.onBoardingStepImage)
        AppViewAssertion.isDisplayed(R.id.onBoardingStepTitle)
        AppViewAssertion.isDisplayed(R.id.onBoardingStepDescription)
        AppViewAssertion.isTextDisplayed("And get notified when you are in place.")
        AppViewAssertion.isTextDisplayed("Now you can save you reminders location based and get notified when you get into or leave the place!")
        AppViewAction.actionSwipeLeft(R.id.onBoardingStepImage)
    }
}
