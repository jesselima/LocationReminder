package com.udacity.locationreminder.locationreminders.localdatasource

import com.udacity.locationreminder.shareddata.localdatasource.models.ReminderData
import com.udacity.locationreminder.shareddata.localdatasource.models.Result
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepository

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : RemindersLocalRepository {

//    TODO: Create a fake data source to act as a double to the real data source

    override suspend fun getReminders(): Result<List<ReminderData>> {
        TODO("Return the reminders")
    }

    override suspend fun saveReminder(reminder: ReminderData) {
        TODO("save the reminder")
    }

    override suspend fun getReminder(id: String): Result<ReminderData> {
        TODO("return the reminder with the id")
    }

    override suspend fun deleteAllReminders() {
        TODO("delete all the reminders")
    }


}