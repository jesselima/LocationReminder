package com.udacity.project4.features.copyright

import android.content.Intent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.util.AppViewAction.onItemListPositionClicked
import com.udacity.project4.util.AppViewAssertion.isTextDisplayed
import com.udacity.project4.util.AppViewAssertion.isViewDisplayed
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class CopyRightsFragmentTest {

    @Test
    fun when_screen_open_should_display_copyrights_content() {
        launchFragmentInContainer<CopyrightFragment>(themeResId = R.style.LocationReminderAppTheme)

        isViewDisplayed(R.id.favoritePicturesAppBarLayout)
        isViewDisplayed(R.id.copyrightTopAppBar)
        isViewDisplayed(R.id.copyrightRecyclerView)
        isTextDisplayed("Copyrights")
    }

    @Test
    fun when_screen_open_should_launch_intent_with_action_view() {
        Intents.init()

        launchFragmentInContainer<CopyrightFragment>(themeResId = R.style.LocationReminderAppTheme)

        onItemListPositionClicked(R.id.copyrightRecyclerView)

        val matcher: Matcher<Intent> = IntentMatchers.hasAction(Intent.ACTION_VIEW)
        Intents.intended(matcher)

        Intents.release()
    }
}
