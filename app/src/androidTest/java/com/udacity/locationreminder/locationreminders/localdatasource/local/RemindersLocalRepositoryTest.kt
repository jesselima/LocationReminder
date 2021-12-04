package com.udacity.locationreminder.locationreminders.localdatasource.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.android.gms.location.Geofence
import com.udacity.locationreminder.shareddata.localdatasource.database.RemindersDatabase
import com.udacity.locationreminder.shareddata.localdatasource.models.ReminderData
import com.udacity.locationreminder.shareddata.localdatasource.models.ResultData
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepositoryImpl
import com.udacity.locationreminder.stubs.reminderData
import com.udacity.locationreminder.stubs.reminderData2
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepositoryImpl

    @Before
    fun initLocalDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        repository = RemindersLocalRepositoryImpl(
            remindersDao = database.reminderDao(),
        )
    }

    @After
    fun closeDatabase() = database.close()

    @Test
    fun getReminders_should_return_all_reminders() = runBlocking {
        // Given
        database.reminderDao().saveReminder(reminderData)
        database.reminderDao().saveReminder(reminderData2)

        // When
        val result = repository.getReminders() as ResultData.Success<*>
        val reminders = result.data as List<*>

        // Then
        assertThat(result, CoreMatchers.notNullValue())
        assertThat(reminders.size,  `is`(2))
    }

    @Test
    fun saveReminder_should_insert_new_reminder() = runBlocking {
        // Given
        val resultInserted = repository.saveReminder(reminderData)

        // When
        val result = database.reminderDao().getReminderById(reminderData.id.toString())

        // Then
        assertThat(result, CoreMatchers.notNullValue())
        assertThat(resultInserted,  `is`(reminderData.id))
        assertThat(result?.id,  `is`(reminderData.id))
    }


    @Test
    fun getReminderById_should_insert_new_reminder() = runBlocking {
        // Given
        val resultInserted = repository.saveReminder(reminderData)

        // When
        val result = database.reminderDao().getReminderById(reminderData.id.toString())

        // Then
        assertThat(result, CoreMatchers.notNullValue())
        assertThat(resultInserted,  `is`(reminderData.id))
        assertThat(result?.id,  `is`(reminderData.id))
        assertThat(result?.title, `is`("Grow tomatoes"))
        assertThat(result?.description, `is`("Do not forget the fertilizer"))
        assertThat(result?.locationName, `is`("Mars, South Pole"))
        assertThat(result?.latitude, `is`(-90.0))
        assertThat(result?.longitude, `is`(-210.0))
        assertThat(result?.isPoi, `is`(true))
        assertThat(result?.poiId, `is`("etAlienHumansTomato"))
        assertThat(result?.circularRadius, `is`(450.0f))
        assertThat(result?.expiration, `is`(-1))
        assertThat(result?.transitionType, `is`(Geofence.GEOFENCE_TRANSITION_EXIT))
        assertThat(result?.isGeofenceEnable, `is`(true))
    }

    @Test
    fun deleteAllReminders_should_delete_all_reminders() = runBlocking {
        // Given
        repository.saveReminder(reminderData)
        repository.saveReminder(reminderData2)

        val result = repository.getReminders() as ResultData.Success<*>
        val savedReminders = result.data as List<*>

        // When
        val numberOfDeletedReminders = repository.deleteAllReminders()
        assertThat(numberOfDeletedReminders,  `is`(2))

        // Then
        val resultEmpty = repository.getReminders() as ResultData.Success<*>
        val deletedRemindersResult = resultEmpty.data as List<*>
        assertThat(savedReminders.size,  `is`(2))
        assertThat(deletedRemindersResult.size,  `is`(0))
    }


    @Test
    fun deleteReminder_should_delete_reminder() = runBlocking {
        // Given
        repository.saveReminder(reminderData)
        val result = repository.getReminder(reminderData.id.toString()) as ResultData.Success<ReminderData>

        // When
        val resultDeleted = repository.deleteReminder(result.data)

        // Then
        assertThat(resultDeleted,  `is`(1))
        assertThat(result.data.id,  `is`(reminderData.id))
    }

    @Test
    fun updateReminderGeofenceStatus_should_update_geofence_status_to_disabled() = runBlocking {
        // Given
        database.reminderDao().saveReminder(reminderData)

        // When
        val updateResult = repository.updateGeofenceStatus(reminderId = reminderData.id ?: 0, isGeofenceEnable = false)
        assertThat(updateResult,  `is`(1))

        val result = database.reminderDao().getReminderById(reminderData.id.toString()) as ReminderData
        assertThat(result, CoreMatchers.notNullValue())
        assertThat(result.title,  `is`(reminderData.title))
        assertThat(result.isGeofenceEnable,  `is`(false))
    }

    @Test
    fun updateReminder_should_update_all_reminder_data_on_database() = runBlocking {
        // Given
        database.reminderDao().saveReminder(reminderData)

        // When
        val result =  database.reminderDao()
                        .getReminderById(reminderData.id.toString()) as ReminderData

        /** Update the reminder instance */
        val updatedReminderData = result.copy(
            title = "Grow potatoes",
            description = "Do not forget the fertilizer and water",
            locationName = "Mars, North Pole",
            latitude = 90.0,
            longitude = 340.0,
            isPoi = false,
            poiId = "etAlienHumansPotatoes",
            circularRadius =  300.0f,
            expiration = 5,
            transitionType = Geofence.GEOFENCE_TRANSITION_ENTER,
            isGeofenceEnable = false
        )

        val updateResult = repository.updateReminder(updatedReminderData)
        assertThat(updateResult,  `is`(1))

        val updatedReminder = database.reminderDao()
            .getReminderById(reminderData.id.toString()) as ReminderData

        // Then
        /** Assert all data have been updated */
        assertThat(updatedReminder, CoreMatchers.notNullValue())
        assertThat(updatedReminder.title,  `is`("Grow potatoes"))
        assertThat(updatedReminder.description,  `is`("Do not forget the fertilizer and water"))
        assertThat(updatedReminder.locationName,  `is`("Mars, North Pole"))
        assertThat(updatedReminder.latitude,  `is`(90.0))
        assertThat(updatedReminder.longitude,  `is`(340.0))
        assertThat(updatedReminder.isPoi,  `is`(false))
        assertThat(updatedReminder.poiId,  `is`("etAlienHumansPotatoes"))
        assertThat(updatedReminder.circularRadius,  `is`( 300.0f))
        assertThat(updatedReminder.expiration,  `is`(5))
        assertThat(updatedReminder.transitionType,  `is`(Geofence.GEOFENCE_TRANSITION_ENTER))
        assertThat(updatedReminder.isGeofenceEnable,  `is`(false))
    }
}