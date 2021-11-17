package com.udacity.locationreminder.locationreminders.data.local

import com.udacity.locationreminder.locationreminders.data.RemindersLocalRepository
import com.udacity.locationreminder.locationreminders.data.dto.ReminderData
import com.udacity.locationreminder.locationreminders.data.dto.Result
import kotlinx.coroutines.*

/**
 * Concrete implementation of a data source as a db.
 *
 * The repository is implemented so that you can focus on only testing it.
 *
 * @param remindersDao the dao that does the Room db operations
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class RemindersLocalRepositoryImpl(
    private val remindersDao: RemindersDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemindersLocalRepository {

    /**
     * Get the reminders list from the local db
     * @return Result the holds a Success with all the reminders or an Error object with the error message
     */
    override suspend fun getReminders(): Result<List<ReminderData>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(remindersDao.getReminders())
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
    }

    /**
     * Insert a reminder in the db.
     * @param reminder the reminder to be inserted
     */
    override suspend fun saveReminder(reminder: ReminderData): Long {
        val result = withContext(ioDispatcher) {
            remindersDao.saveReminder(reminder)
        }
        return result
    }

    /**
     * Get a reminder by its id
     * @param id to be used to get the reminder
     * @return Result the holds a Success object with the Reminder or an Error object with the error message
     */
    override suspend fun getReminder(id: String): Result<ReminderData> = withContext(ioDispatcher) {
        try {
            val reminder = remindersDao.getReminderById(id)
            if (reminder != null) {
                return@withContext Result.Success(reminder)
            } else {
                return@withContext Result.Error("Reminder not found!")
            }
        } catch (e: Exception) {
            return@withContext Result.Error(e.localizedMessage)
        }
    }

    /**
     * Deletes all the reminders in the db
     */
    override suspend fun deleteAllReminders() {
        withContext(ioDispatcher) {
            remindersDao.deleteAllReminders()
        }
    }

    override suspend fun deleteReminder(reminder: ReminderData) : Int {
        return withContext(ioDispatcher) {
            remindersDao.deleteReminder(reminder)
        }
    }

    override suspend fun updateGeofenceStatus(reminderId: Long, isGeofenceEnable: Boolean): Int {
        return withContext(ioDispatcher) {
            remindersDao.updateReminder(reminderId, isGeofenceEnable)
        }
    }

    override suspend fun updateReminder(reminder: ReminderData): Int {
        return withContext(ioDispatcher) {
            remindersDao.updateReminder(reminder)
        }
    }

}
