package com.udacity.project4.shareddata.localdatasource.repository

import com.udacity.project4.shareddata.localdatasource.models.ReminderData
import com.udacity.project4.shareddata.localdatasource.models.ResultData

/**
 * Main entry point for accessing reminders data.
 */
interface RemindersLocalRepository {
    suspend fun getReminders(): ResultData<List<ReminderData>>
    suspend fun saveReminder(reminder: ReminderData): ResultData<Long>
    suspend fun getReminder(id: String): ResultData<ReminderData>
    suspend fun updateGeofenceStatus(reminderId: Long, isGeofenceEnable: Boolean): ResultData<Int>
    suspend fun updateReminder(reminder: ReminderData): ResultData<Int>
    suspend fun deleteReminder(reminder: ReminderData): ResultData<Int>
    suspend fun deleteAllReminders() : ResultData<Int>
}