package com.udacity.locationreminder.locationreminders.localdatasource.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.android.gms.location.Geofence
import com.udacity.locationreminder.shareddata.localdatasource.database.RemindersDatabase
import com.udacity.locationreminder.shareddata.localdatasource.models.ReminderData
import com.udacity.locationreminder.stubs.reminderData
import com.udacity.locationreminder.stubs.reminderData2
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
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
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initLocalDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDatabase() = database.close()

    @Test
    fun saveReminderAndGetReminderById() = runBlocking {
        // Given
        database.reminderDao().saveReminder(reminderData)

        // When
        val result = database.reminderDao().getReminderById(reminderData.id.toString())

        // Then
        assertThat(result as ReminderData, CoreMatchers.notNullValue())
        assertThat(result.title,  `is`("Grow tomatoes"))
        assertThat(result.description,  `is`("Do not forget the fertilizer"))
        assertThat(result.locationName,  `is`("Mars, South Pole"))
        assertThat(result.latitude,  `is`(-90.0))
        assertThat(result.longitude,  `is`(-210.0))
        assertThat(result.isPoi,  `is`(true))
        assertThat(result.poiId,  `is`("etAlienHumansTomato"))
        assertThat(result.circularRadius,  `is`( 450.0f))
        assertThat(result.expiration,  `is`(-1))
        assertThat(result.transitionType,  `is`(Geofence.GEOFENCE_TRANSITION_EXIT))
        assertThat(result.isGeofenceEnable,  `is`(true))
    }

    @Test
    fun updateReminder() = runBlocking {
        // Given
        database.reminderDao().saveReminder(reminderData)

        // When
        val result = database.reminderDao().getReminderById(reminderData.id.toString()) as ReminderData

        /** Assert reminder was saved properly */
        assertThat(result, CoreMatchers.notNullValue())
        assertThat(result.title,  `is`(reminderData.title))
        assertThat(result.description,  `is`(reminderData.description))
        assertThat(result.locationName,  `is`(reminderData.locationName))
        assertThat(result.latitude,  `is`(reminderData.latitude))
        assertThat(result.longitude,  `is`(reminderData.longitude))
        assertThat(result.isPoi,  `is`(reminderData.isPoi))
        assertThat(result.poiId,  `is`(reminderData.poiId))
        assertThat(result.circularRadius,  `is`( reminderData.circularRadius))
        assertThat(result.expiration,  `is`(reminderData.expiration))
        assertThat(result.transitionType,  `is`(reminderData.transitionType))
        assertThat(result.isGeofenceEnable,  `is`(reminderData.isGeofenceEnable))

        /** Update the reminder instance */
        val reminderData = result.copy(
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

        /** Chek the update result - The updateResult will should be the numbers of rows affected */
        val updateResult = database.reminderDao().updateReminder(reminderData)
        assertThat(updateResult,  `is`(1))

        /** Retrieve the updated reminder to check if all values were updated */
        val updatedReminder = database.reminderDao()
                                    .getReminderById(reminderData.id.toString()) as ReminderData

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

    @Test
    fun updateReminderGeofenceStatus() = runBlocking {
        // Given
        database.reminderDao().saveReminder(reminderData)

        // When
        /** Chek the update result - The updateResult will should be the numbers of rows affected */
        val updateResult = database.reminderDao().updateReminder(reminderId = reminderData.id ?: 0, isGeofenceEnable = false)
        assertThat(updateResult,  `is`(1))

        val result = database.reminderDao().getReminderById(reminderData.id.toString()) as ReminderData
        /** Assert reminder was saved properly */
        assertThat(result, CoreMatchers.notNullValue())
        assertThat(result.title,  `is`(reminderData.title))
        assertThat(result.isGeofenceEnable,  `is`(false))
    }

    @Test
    fun getAllReminders() = runBlockingTest {
        // Given
        database.reminderDao().saveReminder(reminderData)
        database.reminderDao().saveReminder(reminderData2)

        // When
        val result = database.reminderDao().getReminders()

        // Then
        assertThat(result, CoreMatchers.notNullValue())
        assertThat(result.size,  `is`(2))
    }

    @Test
    fun deleteAllReminders() = runBlockingTest {
        // Given
        database.reminderDao().saveReminder(reminderData)
        database.reminderDao().saveReminder(reminderData2)

        // When
        val result = database.reminderDao().getReminders()

        // Then
        assertThat(result, CoreMatchers.notNullValue())
        assertThat(result.size,  `is`(2))

        val resultDeleted = database.reminderDao().deleteAllReminders()
        assertThat(resultDeleted,  `is`(2))

        val validateEmptyTableResult = database.reminderDao().getReminders()
        assertThat(validateEmptyTableResult,  `is`(emptyList()))
    }

    @Test
    fun deleteReminder() = runBlockingTest {
        // Given
        database.reminderDao().saveReminder(reminderData)
        database.reminderDao().saveReminder(reminderData2)

        // When
        val deletedResult = database.reminderDao().deleteReminder(reminderData)
        val getReminderResult = database.reminderDao().getReminderById(reminderData.id.toString())
        val remainingReminderResult = database.reminderDao().getReminders()

        // Then
        assertThat(deletedResult,  `is`(1))
        assert(getReminderResult == null)
        assertThat(remainingReminderResult.size,  `is`(1))
    }
}