package com.udacity.locationreminder.core

import android.app.Application
import com.udacity.locationreminder.common.notification.setupNotificationChannel
import com.udacity.locationreminder.core.di.MainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class LocationReminderApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@LocationReminderApp)
            MainModule().initMainModules()
        }

        setupNotificationChannel()
    }
}