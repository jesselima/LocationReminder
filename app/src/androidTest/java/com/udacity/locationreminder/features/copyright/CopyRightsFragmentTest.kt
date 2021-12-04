package com.udacity.locationreminder.features.copyright

import android.content.Intent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.locationreminder.R
import com.udacity.locationreminder.util.AppViewAction
import com.udacity.locationreminder.util.AppViewAssertion
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class CopyRightsFragmentTest {

    @Test
    fun when_screen_open_should_display_copyrights_content() {
        launchFragmentInContainer<CopyrightFragment>(themeResId = R.style.LocationReminderAppTheme)

        AppViewAssertion.isDisplayed(R.id.favoritePicturesAppBarLayout)
        AppViewAssertion.isDisplayed(R.id.copyrightTopAppBar)
        AppViewAssertion.isDisplayed(R.id.copyrightRecyclerView)
        AppViewAssertion.isTextDisplayed("Copyrights")
    }

    @Test
    fun when_screen_open_should_launch_intent_with_action_view() {
        Intents.init()

        launchFragmentInContainer<CopyrightFragment>(themeResId = R.style.LocationReminderAppTheme)

        AppViewAction.onItemListPositionClicked(R.id.copyrightRecyclerView)

        val matcher: Matcher<Intent> = IntentMatchers.hasAction(Intent.ACTION_VIEW)
        Intents.intended(matcher)

        Intents.release()
    }
}
