package com.udacity.locationreminder.core

import android.app.Application
import com.udacity.locationreminder.common.notification.setupNotificationChannel
import com.udacity.locationreminder.features.addreminder.presentation.AddReminderViewModel
import com.udacity.locationreminder.features.addreminder.usecase.InputValidatorsUseCase
import com.udacity.locationreminder.features.reminderdetails.ReminderDetailsViewModel
import com.udacity.locationreminder.features.reminderslist.RemindersListViewModel
import com.udacity.locationreminder.geofence.GeofenceManager
import com.udacity.locationreminder.geofence.GeofenceManagerImpl
import com.udacity.locationreminder.shareddata.localdatasource.database.LocalDatabase
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class LocationReminderApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val domainModule = module {
            factory { InputValidatorsUseCase() }
        }

        val presentationModule = module {
            viewModel {
                AddReminderViewModel(
                    remindersLocalRepository = get(),
                    inputValidatorsUseCase = get(),
                )
            }
            viewModel {
                RemindersListViewModel(
                    remindersLocalRepository = get(),
                )
            }
            viewModel {
                ReminderDetailsViewModel(
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
            factory<GeofenceManager> { GeofenceManagerImpl() }
        }

        startKoin {
            androidContext(this@LocationReminderApp)
            modules(listOf(dataModule, domainModule, presentationModule, geoFenceModule))
        }

        setupNotificationChannel()
    }
}