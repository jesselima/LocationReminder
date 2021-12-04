package com.udacity.locationreminder.features.authentication

import android.content.Intent
import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.firebase.ui.auth.KickoffActivity
import com.udacity.locationreminder.R
import com.udacity.locationreminder.util.AppViewAction
import com.udacity.locationreminder.util.AppViewAssertion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class AuthenticationActivityTest {

    @Test
    fun launchAuthenticationActivity_should_pass_throughout_all_steps_then_call_reminders_screen() {
        /**
         * Initializes Intents and begins recording intents. Must be called prior to triggering any
         * actions that send out intents which need to be verified or stubbed. This is similar to
         * MockitoAnnotations.initMocks.
         */
        Intents.init()

        launchActivity<AuthenticationActivity>()

        AppViewAssertion.isDisplayed(R.id.imageViewLoginBackground)
        AppViewAssertion.isDisplayed(R.id.viewSemiTransparentMask)
        AppViewAssertion.isDisplayed(R.id.textWelcome)
        AppViewAssertion.isDisplayed(R.id.textWelcomeDescription)

        AppViewAssertion.isTextDisplayed("Location Reminder")
        AppViewAssertion.isTextDisplayed(
            "Set your mind free to what matters most and do the " +
                "things at the right time and place."
        )
        AppViewAssertion.isTextDisplayed("LOGIN")
        AppViewAssertion.isDisplayed(R.id.buttonLogin)
        AppViewAction.performClick(R.id.buttonLogin)

        val matcher: Matcher<Intent> = hasComponent(KickoffActivity::class.java.name)
        intended(matcher)

        /** Clears Intents state. Must be called after each test case. */
        Intents.release()
    }
}
