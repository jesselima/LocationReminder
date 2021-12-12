package com.udacity.locationreminder.e2e

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.udacity.locationreminder.BuildConfig
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert

fun UiDevice.assertViewHasText(
    viewId: String,
    text: String,
    currentAppPackage: String = BuildConfig.APPLICATION_ID,
    timeout: Long = 1000,
) {
    val view: UiObject2 = wait(Until.findObject(By.res(currentAppPackage, viewId)), timeout)
    MatcherAssert.assertThat(view.text, CoreMatchers.`is`(CoreMatchers.equalTo(text)))
}

fun UiDevice.waitUntilVisibleAndAssertHasText(
    viewId: String,
    text: String,
    currentAppPackage: String,
    timeout: Long = 1000
) {
    val view: UiObject2 = wait(Until.findObject(By.res(currentAppPackage, viewId)), timeout)
    MatcherAssert.assertThat(view.text, CoreMatchers.`is`(CoreMatchers.equalTo(text)))
}