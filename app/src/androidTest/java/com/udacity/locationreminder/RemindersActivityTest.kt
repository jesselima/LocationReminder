package com.udacity.locationreminder

import android.app.Application
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.locationreminder.shareddata.localdatasource.database.LocalDatabase
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
// Extended Koin Test - embed autoclose @after method to close Koin after every test
class RemindersActivityTest : AutoCloseKoinTest() {

    private lateinit var repository: RemindersLocalRepository
    private lateinit var appContext: Application

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        //stop the original app koin
        stopKoin()
        appContext = getApplicationContext()
        val myModule = module {
//            viewModel {
//                RemindersListViewModel(
//                    appContext,
//                    get() as RemindersLocalRepository
//                )
//            }
//            single {
//                SaveReminderViewModel(
//                    // TODO Fix Me
//                    // appContext,
//                     get() as RemindersLocalRepository
//                )
//            }
            single {
                RemindersLocalRepositoryImpl(remindersDao = get())
            }
            single {
                LocalDatabase.createRemindersDao(appContext)
            }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }


//    TODO: add End to End testing to the app

}
