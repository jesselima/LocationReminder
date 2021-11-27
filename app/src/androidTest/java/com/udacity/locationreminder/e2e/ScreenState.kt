package com.udacity.locationreminder.e2e

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

fun UiDevice.waitUltiScreenLoads(currentAppPackage: String, timeout: Long = 5000) {
    wait(Until.hasObject(By.pkg(currentAppPackage)), timeout)
}