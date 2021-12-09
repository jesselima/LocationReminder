package com.udacity.locationreminder.features.addremindder

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.locationreminder.R
import com.udacity.locationreminder.features.addreminder.presentation.SelectLocationFragment
import com.udacity.locationreminder.stubs.reminderItemView
import com.udacity.locationreminder.util.AppViewAssertion
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
            "Click anywhere on the map or any point\nof interest to select a location."
        )
        AppViewAssertion.isViewIdWithTextDisplayed(
            R.id.buttonGetSelectedLocation, "GET THIS LOCATION"
        )
    }
}
