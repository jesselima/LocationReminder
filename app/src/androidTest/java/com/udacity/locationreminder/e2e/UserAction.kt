package com.udacity.locationreminder.e2e

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector

enum class UserAction {
    HOME, BACK, LEFT, RIGHT, UP, DOWN, CENTER, MENU, SEARCH, ENTER, DELETE, DEL, RECENT,
    VOLUME_UP, VOLUME_DOWN, VOLUME_MUTE, CAMERA;
    fun asKey(): String = this.name.lowercase()
}

fun UiDevice.onViewContainsTextClick(
    text: String
) {
    findObject(UiSelector().textContains(text)).click()
}

fun UiDevice.onViewContainsTextClickAndWait(text: String) {
    findObject(UiSelector().textContains(text)).clickAndWaitForNewWindow()
}

fun UiDevice.onViewTextMatchesClick(text: String) {
    findObject(UiSelector().textMatches(text)).click()
}

fun UiDevice.onViewTextMatchesClickAndWait(text: String) {
    findObject(UiSelector().textMatches(text)).clickAndWaitForNewWindow()
}

fun UiDevice.onViewWithTextClickAndWait(text: String) {
    findObject(UiSelector().text(text)).clickAndWaitForNewWindow()
}

fun UiDevice.onViewWithTextClick(text: String) {
    findObject(UiSelector().text(text)).click()
}

fun UiDevice.onViewWithIdClickAndWait(viewId: String, currentAppPackage: String? = null) {
    currentAppPackage?.let {
        findObject(UiSelector().resourceId("$currentAppPackage:id/$viewId")).clickAndWaitForNewWindow()
    } ?: findObject(UiSelector().resourceId(viewId)).clickAndWaitForNewWindow()
}

fun UiDevice.onViewWithIdPerformClick(viewId: String, currentAppPackage: String) {
    findObject(UiSelector().resourceId("$currentAppPackage:id/$viewId")).click()
}

fun UiDevice.onViewWithIdPerformTypeText(
    viewId: String,
    text: String,
    currentAppPackage: String
) {
    findObject(By.res(currentAppPackage, viewId)).text =  text
}

fun UiDevice.onViewWithIdPerformSwipe(
    viewId: String,
    currentAppPackage: String,
    numberOfSwipes: Int = 1
) {
    val view = findObject(UiSelector().resourceId("$currentAppPackage:id/$viewId"))
    with(view) {
        for (swipe in 1..numberOfSwipes) {
            swipe(
                bounds.right - 5,
                bounds.centerY(),
                bounds.left / 4,
                bounds.centerY(), 10
            )
        }
    }
}