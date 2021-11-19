package com.udacity.locationreminder.shareddata.localdatasource.repository

import com.udacity.locationreminder.shareddata.localdatasource.models.ReminderData
import com.udacity.locationreminder.shareddata.localdatasource.models.Result

/**
 * Main entry point for accessing reminders data.
 */
interface RemindersLocalRepository {
    suspend fun getReminders(): Result<List<ReminderData>>
    suspend fun saveReminder(reminder: ReminderData): Long
    suspend fun getReminder(id: String): Result<ReminderData>
    suspend fun updateGeofenceStatus(reminderId: Long, isGeofenceEnable: Boolean): Int
    suspend fun updateReminder(reminder: ReminderData): Int
    suspend fun deleteReminder(reminder: ReminderData): Int
    suspend fun deleteAllReminders()
}