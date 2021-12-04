package com.udacity.locationreminder.features.onboarding

import android.content.Intent
import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.locationreminder.R
import com.udacity.locationreminder.features.RemindersActivity
import com.udacity.locationreminder.util.AppViewAction
import com.udacity.locationreminder.util.AppViewAssertion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class ActivityOnBoardingTest {

    @Test
    fun launchOnBoardingActivity_should_pass_throughout_all_steps_then_call_reminders_screen() {
        /**
         * Initializes Intents and begins recording intents. Must be called prior to triggering any
         * actions that send out intents which need to be verified or stubbed. This is similar to
         * MockitoAnnotations.initMocks.
         */
        Intents.init()

        launchActivity<OnBoardingActivity>()

        AppViewAssertion.isDisplayed(R.id.onBoardingViewPager)
        AppViewAssertion.isDisplayed(R.id.tabLayout)

        AppViewAction.actionSwipeLeft(R.id.onBoardingStepImage)
        AppViewAction.actionSwipeLeft(R.id.onBoardingStepImage)

        AppViewAssertion.isDisplayed(R.id.onboardingButtonStart)
        AppViewAssertion.isTextDisplayed("Start")
        AppViewAction.performClick(R.id.onboardingButtonStart)

        val matcher: Matcher<Intent> = hasComponent(RemindersActivity::class.java.name)

        /**
         * Asserts that the given matcher matches one and only one intent sent by the application under
         * test. This is an equivalent of verify(mock, times(1)) in Mockito. Verification does not have to
         * occur in the same order as the intents were sent. Intents are recorded from the time that
         * Intents.init is called.
         */
        intended(matcher)

        /** Clears Intents state. Must be called after each test case. */
        Intents.release()
    }
}
