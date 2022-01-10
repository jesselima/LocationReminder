package com.udacity.project4.features.addremindder

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.features.addreminder.presentation.SelectLocationFragment
import com.udacity.project4.stubs.reminderItemView
import com.udacity.project4.util.AppViewAssertion
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class SelectLocationFragmentTest {

    @Test
    fun screenOpen_should_display_mapInfo() {
        launchFragmentInContainer<SelectLocationFragment>(
            fragmentArgs = bundleOf(
                "lastSelectedLocation" to reminderItemView,
            ),
            themeResId = R.style.LocationReminderAppTheme
        )

        AppViewAssertion.isTextDisplayed("Select location")
        AppViewAssertion.isViewIdWithTextDisplayed(
            R.id.textViewHowToSetLocation,
            "Click anywhere on the map or any point of interest to select a location."
        )
        AppViewAssertion.isViewIdWithTextDisplayed(
            R.id.buttonGetSelectedLocation, "GET THIS LOCATION"
        )
    }
}
