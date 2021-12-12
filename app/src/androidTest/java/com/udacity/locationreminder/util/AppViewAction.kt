package com.udacity.locationreminder.util

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.udacity.locationreminder.R
import org.hamcrest.Matcher

object AppViewAction {

    fun performClick(@IdRes resId: Int) {
        Espresso.onView(ViewMatchers.withId(resId)).perform(click())
    }

    fun performClick(text: String) {
        Espresso.onView(ViewMatchers.withText(text)).perform(click())
    }

    fun performType(@IdRes resId: Int, text: String?) {
        Espresso.onView(ViewMatchers.withId(resId)).perform(ViewActions.typeText(text))
        Espresso.closeSoftKeyboard()
    }

    fun openActionMenu() {
        Espresso.onIdle {  }
    }

    fun scrollToViewWithId(@IdRes resId: Int) {
        Espresso.onView(ViewMatchers.withId(resId)).perform(ViewActions.scrollTo())
    }

    fun actionSwipeLeft(@IdRes viewId: Int) {
        Espresso.onView(ViewMatchers.withId(viewId)).perform(swipeLeft());
    }

    fun actionSwipeUp(@IdRes viewId: Int) {
        Espresso.onView(ViewMatchers.withId(viewId)).perform(swipeUp());
    }

    fun actionSwipeDown(@IdRes viewId: Int) {
        Espresso.onView(ViewMatchers.withId(viewId)).perform(swipeDown());
    }

    fun actionSwipeRight(@IdRes viewId: Int) {
        Espresso.onView(ViewMatchers.withId(viewId)).perform(swipeRight());
    }

    fun onItemListPositionClicked(@IdRes recyclerViewResId: Int, position: Int = 2) {
        Espresso.onView(ViewMatchers.withId(recyclerViewResId))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
    }

    fun clickOnItemAtPosition(position: Int = 0) {
        Espresso.onView(ViewMatchers.withId(R.id.reminderssRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    position,
                    click()
                )
            )
    }

    fun clickChildViewWithId(@IdRes recyclerViewId: Int, @IdRes viewId: Int, position: Int = 0) {
        Espresso.onView(ViewMatchers.withId(recyclerViewId))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
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