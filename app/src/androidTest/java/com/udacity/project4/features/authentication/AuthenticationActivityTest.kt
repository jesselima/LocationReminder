package com.udacity.project4.features.authentication

import android.content.Intent
import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.firebase.ui.auth.KickoffActivity
import com.udacity.project4.R
import com.udacity.project4.util.AppViewAction
import com.udacity.project4.util.AppViewAssertion.isTextDisplayed
import com.udacity.project4.util.AppViewAssertion.isViewDisplayed
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

        isViewDisplayed(R.id.imageViewLoginBackground)
        isViewDisplayed(R.id.viewSemiTransparentMask)
        isViewDisplayed(R.id.textWelcome)
        isViewDisplayed(R.id.textWelcomeDescription)

        isTextDisplayed("Location Reminder")
        isTextDisplayed(
            "Set your mind free to what matters most and do the " +
                "things at the right time and place."
        )
        isTextDisplayed("LOGIN")
        isViewDisplayed(R.id.buttonLogin)
        AppViewAction.performClick(R.id.buttonLogin)

        val matcher: Matcher<Intent> = hasComponent(KickoffActivity::class.java.name)
        intended(matcher)

        /** Clears Intents state. Must be called after each test case. */
        Intents.release()
    }
}
