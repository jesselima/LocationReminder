package com.udacity.locationreminder.e2e

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.udacity.locationreminder.BuildConfig
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers

/** Launch the App */

fun launchApp(appPackageName: String = BuildConfig.APPLICATION_ID) {
    ApplicationProvider.getApplicationContext<Context?>()?.run {
        packageManager.getLaunchIntentForPackage(appPackageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(this)
        }
    }
}

fun UiDevice.launchApp(appPackageName: String = BuildConfig.APPLICATION_ID) {
    pressHome()

    /** Launch the App INTERNAL_STORAGE_SETTINGS*/
    val context: Context = ApplicationProvider.getApplicationContext()
    val intent: Intent? = context.packageManager
        .getLaunchIntentForPackage(appPackageName)
    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(intent)

    /** Wait for the app to appear */
    wait(Until.hasObject(By.pkg(appPackageName)), 5000)
}


fun UiDevice.openAppSettings(appPackageName: String = BuildConfig.APPLICATION_ID) {
    val context: Context = ApplicationProvider.getApplicationContext()
    context.startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", appPackageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    })
    /** Wait for the app to appear */
    wait(Until.hasObject(By.pkg(appPackageName)), 5000)
}

fun UiDevice.assertLauncherPackageWaitAppLaunch(launcherPackage: String) {
    MatcherAssert.assertThat(launcherPackageName, Matchers.notNullValue())
    wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), 5000)
}
