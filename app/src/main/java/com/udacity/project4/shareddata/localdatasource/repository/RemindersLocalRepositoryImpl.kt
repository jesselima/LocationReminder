package com.udacity.project4.shareddata.localdatasource.repository

import com.udacity.project4.shareddata.localdatasource.database.RemindersDao
import com.udacity.project4.shareddata.localdatasource.models.ReminderData
import com.udacity.project4.shareddata.localdatasource.models.ResultData
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
     * @return ResultData the holds a Success with all the reminders or an Error object with the error message
     */
    override suspend fun getReminders(): ResultData<List<ReminderData>> = withContext(ioDispatcher) {
        return@withContext try {
            ResultData.Success(remindersDao.getReminders())
        } catch (ex: Exception) {
            ResultData.Error(ex.localizedMessage)
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
     * @return ResultData the holds a Success object with the Reminder or an Error object with the error message
     */
    override suspend fun getReminder(id: String): ResultData<ReminderData> = withContext(ioDispatcher) {
        try {
            val reminder = remindersDao.getReminderById(id)
            if (reminder != null) {
                return@withContext ResultData.Success(reminder)
            } else {
                return@withContext ResultData.Error("Reminder not found!")
            }
        } catch (e: Exception) {
            return@withContext ResultData.Error(e.localizedMessage)
        }
    }

    /**
     * Deletes all the reminders in the db
     */
    override suspend fun deleteAllReminders(): Int {
        return withContext(ioDispatcher) {
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
