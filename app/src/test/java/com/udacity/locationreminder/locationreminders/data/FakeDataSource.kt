package com.udacity.locationreminder.locationreminders.data

import com.udacity.locationreminder.locationreminders.data.dto.ReminderData
import com.udacity.locationreminder.locationreminders.data.dto.Result

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