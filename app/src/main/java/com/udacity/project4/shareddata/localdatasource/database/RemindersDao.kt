package com.udacity.project4.shareddata.localdatasource.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.udacity.project4.shareddata.localdatasource.models.ReminderData

/**
 * Data Access Object for the reminders table.
 */
@Dao
interface RemindersDao {
    /**
     * @return all reminders.
     */
    @Query("SELECT * FROM reminders")
    fun getReminders(): List<ReminderData>?

    /**
     * @param reminderId the id of the reminder
     * @return the reminder object with the reminderId
     */
    @Query("SELECT * FROM reminders where id = :reminderId")
    fun getReminderById(reminderId: String): ReminderData?

    /**
     * Insert a reminder in the database. If the reminder already exists, replace it.
     *
     * @param reminder the reminder to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReminder(reminder: ReminderData): Long?

    /**
     * Delete all reminders.
     */
    @Query("DELETE FROM reminders")
    fun deleteAllReminders(): Int?

    /**
     * Delete all reminders.
     */
    @Delete
    fun deleteReminder(reminder: ReminderData): Int?

    @Query("UPDATE reminders SET is_geofence_enable = :isGeofenceEnable WHERE id = :reminderId")
    fun updateReminder(reminderId: Long, isGeofenceEnable: Boolean): Int?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateReminder(reminder: ReminderData): Int?
}