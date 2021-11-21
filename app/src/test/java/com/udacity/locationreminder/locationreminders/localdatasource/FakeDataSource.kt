package com.udacity.locationreminder.locationreminders.localdatasource

import com.udacity.locationreminder.shareddata.localdatasource.models.ReminderData
import com.udacity.locationreminder.shareddata.localdatasource.models.ResultData
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepository

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : RemindersLocalRepository {

    // TODO: Create a fake data source to act as a double to the real data source

    override suspend fun getReminders(): ResultData<List<ReminderData>> {
        return ResultData.Success(
            data = listOf(
                ReminderData(
                    id=  null,
                    title = "",
                    description = "",
                    locationName = "",
                    latitude = 0.0,
                    longitude =  0.0,
                    isPoi =  false,
                    poiId =  "",
                    circularRadius =  0.0f,
                    expiration =  1L,
                    transitionType=  0,
                    isGeofenceEnable=  false
                )
            )
        )
    }

    override suspend fun saveReminder(reminder: ReminderData) : Long {
        // TODO("save the reminder")
        return 1
    }

    override suspend fun getReminder(id: String): ResultData<ReminderData> {
        // TODO("return the reminder with the id")
        return ResultData.Success(
            data = ReminderData(
                id=  null,
                title = "",
                description = "",
                locationName = "",
                latitude = 0.0,
                longitude =  0.0,
                isPoi =  false,
                poiId =  "",
                circularRadius =  0.0f,
                expiration =  1L,
                transitionType=  0,
                isGeofenceEnable=  false
            )
        )
    }

    override suspend fun updateGeofenceStatus(reminderId: Long, isGeofenceEnable: Boolean): Int {
        // TODO("Not yet implemented")
        return 1
    }

    override suspend fun updateReminder(reminder: ReminderData): Int {
        // TODO("Not yet implemented")
        return 1
    }

    override suspend fun deleteReminder(reminder: ReminderData): Int {
        // TODO("Not yet implemented")
        return 1
    }

    override suspend fun deleteAllReminders(): Int {
        // TODO("delete all the reminders")
        return 0
    }


}