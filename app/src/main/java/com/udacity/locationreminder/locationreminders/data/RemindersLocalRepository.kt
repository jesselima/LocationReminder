package com.udacity.locationreminder.locationreminders.data

import com.udacity.locationreminder.locationreminders.data.dto.ReminderData
import com.udacity.locationreminder.locationreminders.data.dto.Result

/**
 * Main entry point for accessing reminders data.
 */
interface RemindersLocalRepository {
    suspend fun getReminders(): Result<List<ReminderData>>
    suspend fun saveReminder(reminder: ReminderData)
    suspend fun getReminder(id: String): Result<ReminderData>
    suspend fun deleteAllReminders()
}