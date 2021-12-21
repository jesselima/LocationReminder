package com.udacity.project4.shareddata.localdatasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.udacity.project4.shareddata.localdatasource.models.ReminderData

/**
 * The Room Database that contains the reminders table.
 */
@Database(entities = [ReminderData::class], version = 1, exportSchema = false)
abstract class RemindersDatabase : RoomDatabase() {

    abstract fun reminderDao(): RemindersDao
}