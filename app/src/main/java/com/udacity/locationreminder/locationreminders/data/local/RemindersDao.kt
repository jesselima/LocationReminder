package com.udacity.locationreminder.locationreminders.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.locationreminder.locationreminders.data.dto.ReminderData

/**
 * Data Access Object for the reminders table.
 */
@Dao
interface RemindersDao {
    /**
     * @return all reminders.
     */
    @Query("SELECT * FROM reminders")
    fun getReminders(): List<ReminderData>

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
    fun saveReminder(reminder: ReminderData): Long

    /**
     * Delete all reminders.
     */
    @Query("DELETE FROM reminders")
    fun deleteAllReminders()

}