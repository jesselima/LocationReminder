package com.udacity.project4.core.di

import com.udacity.project4.features.addreminder.presentation.AddReminderViewModel
import com.udacity.project4.features.addreminder.domain.usecase.InputValidatorsUseCase
import com.udacity.project4.features.reminderdetails.ReminderDetailsViewModel
import com.udacity.project4.features.reminderslist.RemindersListViewModel
import com.udacity.project4.geofence.GeofenceManager
import com.udacity.project4.geofence.GeofenceManagerImpl
import com.udacity.project4.geofence.GeofenceHandlerHelper
import com.udacity.project4.shareddata.localdatasource.database.LocalDatabase
import com.udacity.project4.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.project4.shareddata.localdatasource.repository.RemindersLocalRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

class MainModule {

    fun initMainModules() {
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
            factory { GeofenceHandlerHelper(context = get()) }
        }

        loadKoinModules(listOf(domainModule, presentationModule, dataModule, geoFenceModule))
    }
}