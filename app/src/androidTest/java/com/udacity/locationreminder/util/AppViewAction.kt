package com.udacity.locationreminder.util

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers

object AppViewAction {

    fun performClick(@IdRes resId: Int) {
        onView(ViewMatchers.withId(resId)).perform(ViewActions.click())
    }

    fun scrollTo(@IdRes resId: Int) {
        onView(ViewMatchers.withId(resId)).perform(ViewActions.scrollTo())
    }

    fun typeText(@IdRes resId: Int, text: String) {
        onView(ViewMatchers.withId(resId)).perform(ViewActions.typeText(text))
    }
}