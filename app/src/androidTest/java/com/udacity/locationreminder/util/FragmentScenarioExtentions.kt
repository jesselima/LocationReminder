package com.udacity.locationreminder.util

import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import junit.framework.Assert


inline fun <reified F : Fragment> FragmentScenario<F>.shouldNavigateTo(
    @IdRes onClickedViewWithResId: Int? = null,
    @IdRes destinationResId: Int,
    @NavigationRes navGraph: Int
) {
    val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    onFragment { fragment ->
        navController.setGraph(navGraph)
        Navigation.setViewNavController(fragment.requireView(), navController)
    }

    onClickedViewWithResId?.let {
        Espresso.onView(ViewMatchers.withId(onClickedViewWithResId)).perform(ViewActions.click())
    }

    Assert.assertEquals(navController.currentDestination?.id, destinationResId)
}