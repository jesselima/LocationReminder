package com.udacity.locationreminder.util

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId

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
}