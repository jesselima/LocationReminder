package com.udacity.project4.core

import android.app.Application
import com.udacity.project4.common.notification.setupNotificationChannel
import com.udacity.project4.core.di.MainModule
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