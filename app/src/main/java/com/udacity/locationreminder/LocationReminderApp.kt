package com.udacity.locationreminder

import android.app.Application
import com.udacity.locationreminder.locationreminders.data.RemindersLocalRepository
import com.udacity.locationreminder.locationreminders.data.local.LocalDatabase
import com.udacity.locationreminder.locationreminders.data.local.RemindersLocalRepositoryImpl
import com.udacity.locationreminder.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.locationreminder.locationreminders.addreminder.SharedReminderViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class LocationReminderApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val presentationModule = module {
            viewModel {
                SharedReminderViewModel(
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

        startKoin {
            androidContext(this@LocationReminderApp)
            modules(listOf(presentationModule, dataModule))
        }
    }
}