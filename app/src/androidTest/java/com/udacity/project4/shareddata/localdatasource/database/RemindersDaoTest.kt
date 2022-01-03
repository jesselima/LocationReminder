package com.udacity.project4.shareddata.localdatasource.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.android.gms.location.Geofence
import com.udacity.project4.shareddata.localdatasource.models.ReminderData
import com.udacity.project4.stubs.reminderData1
import com.udacity.project4.stubs.reminderData2
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
import kotlin.test.assertTrue

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
    fun saveReminder_should_return_its_id_reminder_and_save_all_data() = runBlocking {
        // Given
        val reminderId = database.reminderDao().saveReminder(reminderData1)

        // When
        val result = database.reminderDao().getReminderById(reminderData1.id.toString())

        // Then
        assertThat(result as ReminderData, CoreMatchers.notNullValue())
        assertThat(result.id,  `is`(reminderId))
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
    fun getReminderById_should_return_null_when_reminder_does_not_exists() = runBlockingTest {
        // When
        val result = database.reminderDao().getReminderById("9999999")

        // Then
        assertThat(result, CoreMatchers.nullValue())
    }

    @Test
    fun updateReminder_should_update_all_reminder_data_on_database() = runBlocking {
        // Given
        database.reminderDao().saveReminder(reminderData1)

        // When
        val result = database.reminderDao().getReminderById(reminderData1.id.toString()) as ReminderData

        /** Assert reminder was saved properly */
        assertThat(result, CoreMatchers.notNullValue())
        assertThat(result.title,  `is`(reminderData1.title))
        assertThat(result.description,  `is`(reminderData1.description))
        assertThat(result.locationName,  `is`(reminderData1.locationName))
        assertThat(result.latitude,  `is`(reminderData1.latitude))
        assertThat(result.longitude,  `is`(reminderData1.longitude))
        assertThat(result.isPoi,  `is`(reminderData1.isPoi))
        assertThat(result.poiId,  `is`(reminderData1.poiId))
        assertThat(result.circularRadius,  `is`( reminderData1.circularRadius))
        assertThat(result.expiration,  `is`(reminderData1.expiration))
        assertThat(result.transitionType,  `is`(reminderData1.transitionType))
        assertThat(result.isGeofenceEnable,  `is`(reminderData1.isGeofenceEnable))

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
    fun updateReminderGeofenceStatus_should_update_geofence_status_to_disabled() = runBlocking {
        // Given
        database.reminderDao().saveReminder(reminderData1)

        // When
        /** Chek the update result - The updateResult will should be the numbers of rows affected */
        val updateResult = database.reminderDao().updateReminder(reminderId = reminderData1.id ?: 0, isGeofenceEnable = false)
        assertThat(updateResult,  `is`(1))

        val result = database.reminderDao().getReminderById(reminderData1.id.toString()) as ReminderData
        /** Assert reminder was saved properly */
        assertThat(result, CoreMatchers.notNullValue())
        assertThat(result.title,  `is`(reminderData1.title))
        assertThat(result.isGeofenceEnable,  `is`(false))
    }

    @Test
    fun updateReminder_should_return_no_update_result_when_reminder_does_not_exists() = runBlockingTest {
        // When
        val result = database.reminderDao().updateReminder(reminderId = 9999999, isGeofenceEnable = false)

        // Then
        assertThat(result,  `is`(0))
    }

    @Test
    fun getAllReminders_should_return_all_reminders() = runBlockingTest {
        // Given
        database.reminderDao().saveReminder(reminderData1)
        database.reminderDao().saveReminder(reminderData2)

        // When
        val result = database.reminderDao().getReminders()

        // Then
        assertThat(result, CoreMatchers.notNullValue())
        assertThat(result.size,  `is`(2))
    }

    @Test
    fun getAllReminders_should_return_no_data_when_database_has_no_reminders() = runBlockingTest {
        // When
        val result = database.reminderDao().getReminders()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun deleteAllReminders_should_delete_all_reminders() = runBlockingTest {
        // Given
        database.reminderDao().saveReminder(reminderData1)
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
    fun deleteAllReminders_should_return_no_result_when_there_is_no_reminders() = runBlockingTest {
        val resultDeleted = database.reminderDao().deleteAllReminders()
        assertThat(resultDeleted,  `is`(0))
    }

    @Test
    fun deleteReminder_should_delete_reminder() = runBlockingTest {
        // Given
        database.reminderDao().saveReminder(reminderData1)
        database.reminderDao().saveReminder(reminderData2)

        // When
        val deletedResult = database.reminderDao().deleteReminder(reminderData1)
        val getReminderResult = database.reminderDao().getReminderById(reminderData1.id.toString())
        val remainingReminderResult = database.reminderDao().getReminders()

        // Then
        assertThat(deletedResult,  `is`(1))
        assert(getReminderResult == null)
        assertThat(remainingReminderResult.size,  `is`(1))
    }

    @Test
    fun deleteReminder_should_should_return_no_result_when_reminder_does_not_exists() = runBlockingTest {
        val resultDeleted = database.reminderDao().deleteReminder(reminderData1)
        assertThat(resultDeleted,  `is`(0))
    }
}