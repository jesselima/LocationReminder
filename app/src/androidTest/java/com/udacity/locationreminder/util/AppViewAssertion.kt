package com.udacity.locationreminder.util

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import com.google.android.material.slider.Slider
import org.hamcrest.Description
import org.hamcrest.Matcher

object AppViewAssertion {

    fun isViewDisplayed(@IdRes resId: Int) {
        Espresso.onView(ViewMatchers.withId(resId))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun isTextDisplayed(text: String) {
        Espresso.onView(ViewMatchers.withText(text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun isTextNotDisplayed(text: String) {
        Espresso.onView(ViewMatchers.withText(text)).check(ViewAssertions.doesNotExist())
    }

    fun isTextNotDisplayed(@IdRes resId: Int) {
        Espresso.onView(ViewMatchers.withId(resId)).check(ViewAssertions.doesNotExist())
    }

    fun isViewGone(@IdRes resId: Int) {
        Espresso.onView(ViewMatchers.withId(resId))
            .check(ViewAssertions.matches(
                withEffectiveVisibility(ViewMatchers.Visibility.GONE))
            )
    }

    fun isViewInvisible(@IdRes resId: Int) {
        Espresso.onView(ViewMatchers.withId(resId))
            .check(ViewAssertions.matches(
                withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE))
            )
    }

    fun isViewIdWithTextDisplayed(@IdRes viewId: Int, text: String) {
        Espresso.onView(ViewMatchers.withId(viewId))
            .check(ViewAssertions.matches(ViewMatchers.withText(text)))
    }

    fun assertSlider(@IdRes sliderId: Int, initialValue: Float, onChangedValue: Float) {
        Espresso.onView(ViewMatchers.withId(sliderId)).check(ViewAssertions.matches(hasSliderValue(initialValue)))
        Espresso.onView(ViewMatchers.withId(sliderId)).perform(setSliderValue(onChangedValue))
        Espresso.onView(ViewMatchers.withId(sliderId)).check(ViewAssertions.matches(hasSliderValue(onChangedValue)))
    }

    fun setSliderToValue(@IdRes sliderId: Int, onChangedValue: Float) {
        Espresso.onView(ViewMatchers.withId(sliderId)).perform(setSliderValue(onChangedValue))
    }

    fun assertSliderToValue(@IdRes sliderId: Int, value: Float) {
        Espresso.onView(ViewMatchers.withId(sliderId)).check(ViewAssertions.matches(hasSliderValue(value)))
    }

    private fun hasSliderValue(expectedValue: Float): Matcher<View?> {
        return object : BoundedMatcher<View?, Slider>(Slider::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("expected: $expectedValue")
            }

            override fun matchesSafely(slider: Slider?): Boolean {
                return slider?.value == expectedValue
            }
        }
    }

    private fun setSliderValue(value: Float): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Set Slider value to $value"
            }

            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(Slider::class.java)
            }

            override fun perform(uiController: UiController?, view: View) {
                val seekBar = view as Slider
                seekBar.value = value
            }
        }
    }

    /** By Gildor: https://gist.github.com/gildor/d371f8dcd17b0d398d698d28ab9fd3f3 */
    fun onViewWithTimeout(
        retries: Int = 10,
        retryDelayMs: Long = 500,
        retryAssertion: ViewAssertion = ViewAssertions.matches(
            withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
        ),
        matcher: Matcher<View>
    ): ViewInteraction {
        repeat(retries) { i ->
            try {
                val viewInteraction = Espresso.onView(matcher)
                viewInteraction.check(retryAssertion)
                return viewInteraction
            } catch (e: NoMatchingViewException) {
                if (i >= retries) {
                    throw e
                } else {
                    Thread.sleep(retryDelayMs)
                }
            }
        }
        throw AssertionError("View matcher is broken for $matcher")
    }
}