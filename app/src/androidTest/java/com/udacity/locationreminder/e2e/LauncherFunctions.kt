package com.udacity.locationreminder.e2e

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers

/** Launch the App */

fun launchApp(appPackageName: String) {
    ApplicationProvider.getApplicationContext<Context?>()?.run {
        packageManager.getLaunchIntentForPackage(appPackageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(this)
        }
    }
}

fun UiDevice.assertLauncherPackageWaitAppLaunch(launcherPackage: String) {
    MatcherAssert.assertThat(launcherPackageName, Matchers.notNullValue())
    wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), 5000)
}
