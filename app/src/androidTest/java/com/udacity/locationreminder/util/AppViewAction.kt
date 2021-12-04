package com.udacity.locationreminder.util

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.Matcher

object AppViewAction {

    fun performClick(@IdRes resId: Int) {
        onView(withId(resId)).perform(ViewActions.click())
    }

    fun typeText(@IdRes resId: Int, text: String) {
        onView(ViewMatchers.withId(resId)).perform(ViewActions.typeText(text))
    }

    fun scrollTo(@IdRes resId: Int) {
        onView(withId(resId)).perform(ViewActions.scrollTo())
    }

    fun actionSwipeLeft(@IdRes viewId: Int) {
        onView(withId(viewId)).perform(swipeLeft());
    }

    fun actionSwipeUp(@IdRes viewId: Int) {
        onView(withId(viewId)).perform(swipeUp());
    }

    fun actionSwipeDown(@IdRes viewId: Int) {
        onView(withId(viewId)).perform(swipeDown());
    }

    fun actionSwipeRight(@IdRes viewId: Int) {
        onView(withId(viewId)).perform(swipeRight());
    }

    /** Recycler View Actions */
    fun onItemListPositionClicked(@IdRes recyclerViewResId: Int, position: Int = 2) {
        onView(withId(recyclerViewResId))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
    }

    fun clickChildViewWithId(@IdRes recyclerViewId: Int, @IdRes viewId: Int, position: Int = 0) {
        onView(withId(recyclerViewId))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(
                position,
                actionClickChildViewWithId(viewId)
            )
        )
    }

    private fun actionClickChildViewWithId(@IdRes viewId: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }
            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }
            override fun perform(uiController: UiController?, view: View) {
                val targetView: View = view.findViewById(viewId)
                targetView.performClick()
            }
        }
    }
}