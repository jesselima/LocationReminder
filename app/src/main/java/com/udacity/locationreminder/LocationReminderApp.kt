package com.udacity.locationreminder

import android.app.Application
import com.udacity.locationreminder.locationreminders.data.RemindersLocalRepository
import com.udacity.locationreminder.locationreminders.data.local.LocalDatabase
import com.udacity.locationreminder.locationreminders.data.local.RemindersLocalRepositoryImpl
import com.udacity.locationreminder.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.locationreminder.locationreminders.addreminder.AddReminderViewModel
import com.udacity.locationreminder.locationreminders.geofence.GeofenceManager
import com.udacity.locationreminder.locationreminders.geofence.GeofenceManagerImpl
import com.udacity.locationreminder.utils.setupNotificationChannel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class LocationReminderApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val presentationModule = module {
            viewModel {
                AddReminderViewModel(
                    remindersLocalRepository = get(),
                )
            }
            viewModel {
                RemindersListViewModel(
                    remindersLocalRepository = get(),
                )
            }
        }

        val dataModule = module {
            single { LocalDatabase.createRemindersDao(context = get()) }
            factory<RemindersLocalRepository> {
                RemindersLocalRepositoryImpl(remindersDao = get())
            }
        }

        val geoFenceModule = module {
            factory<GeofenceManager> { GeofenceManagerImpl(context = get()) }
        }

        startKoin {
            androidContext(this@LocationReminderApp)
            modules(listOf(presentationModule, dataModule, geoFenceModule))
        }

        setupNotificationChannel()
    }
}