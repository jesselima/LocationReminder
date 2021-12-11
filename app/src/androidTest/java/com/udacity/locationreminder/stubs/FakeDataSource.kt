package com.udacity.locationreminder.stubs

import com.udacity.locationreminder.shareddata.localdatasource.models.ReminderData
import com.udacity.locationreminder.shareddata.localdatasource.models.ResultData
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepository

class FakeRemindersLocalRepositoryImpl : RemindersLocalRepository {

    var shouldReturnError: Boolean = false

    override suspend fun getReminders(): ResultData<List<ReminderData>> {
        return if (shouldReturnError) {
            shouldReturnError = false
            ResultData.Error(message = "Ooops!")
        } else ResultData.Success(listOf(reminderData1, reminderData2))

    }

    override suspend fun saveReminder(reminder: ReminderData) : Long {
        return if (shouldReturnError) {
            shouldReturnError = false
            0
        } else 8778
    }

    override suspend fun getReminder(id: String): ResultData<ReminderData> {
        return if (shouldReturnError) {
            shouldReturnError = false
            ResultData.Error(message = "Ooops!")
        }
        else ResultData.Success(reminderData1)
    }

    override suspend fun updateGeofenceStatus(reminderId: Long, isGeofenceEnable: Boolean): Int {
        return if (shouldReturnError) {
            shouldReturnError = false
            0
        } else 1
    }

    override suspend fun updateReminder(reminder: ReminderData): Int {
        return if (shouldReturnError) {
            shouldReturnError = false
            0
        } else 1
    }

    override suspend fun deleteReminder(reminder: ReminderData): Int {
        return if (shouldReturnError) {
            shouldReturnError = false
            0
        } else 1
    }

    override suspend fun deleteAllReminders(): Int {
        return if (shouldReturnError) {
            shouldReturnError = false
            0
        } else 5
    }
}