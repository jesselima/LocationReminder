package com.udacity.project4.shareddata.localdatasource.repository

import com.udacity.project4.shareddata.localdatasource.database.RemindersDao
import com.udacity.project4.shareddata.localdatasource.models.ReminderData
import com.udacity.project4.shareddata.localdatasource.models.ResultData
import kotlinx.coroutines.*

const val NO_REMINDERS_DATA_FOUND = 0
const val NO_DATA_FOUND = -1
const val RESULT_ERROR = -1
const val NO_DATA_DELETED_ERROR = 0
const val UPDATE_SUCCESS = 1
const val SAVE_SUCCESS = 1


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
        try {
            val result = remindersDao.getReminders()
            if (result != null) {
                if (result.isNotEmpty()) {
                    return@withContext ResultData.Success(result)
                } else {
                    return@withContext ResultData.Error(
                        message = "Reminder not found!",
                        statusCode = NO_REMINDERS_DATA_FOUND
                    )
                }
            } else {
                return@withContext ResultData.Error(
                    message ="Reminder not found!",
                    statusCode = RESULT_ERROR
                )
            }
        } catch (e: Exception) {
            return@withContext ResultData.Error(e.localizedMessage)
        }
    }

    /**
     * Insert a reminder in the db.
     * @param reminder the reminder to be inserted
     */
    override suspend fun saveReminder(reminder: ReminderData): ResultData<Long> = withContext(ioDispatcher) {
        try {
            val result = remindersDao.saveReminder(reminder)
            if (result != null && result >= SAVE_SUCCESS) {
                return@withContext ResultData.Success(result)
            } else {
                return@withContext ResultData.Error(
                    message = "Error saving reminder",
                    statusCode = RESULT_ERROR
                )
            }
        } catch (e: Exception) {
            return@withContext ResultData.Error(e.localizedMessage)
        }
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
                return@withContext ResultData.Error(
                    message ="Reminder not found!",
                    statusCode = NO_DATA_FOUND
                )
            }
        } catch (e: Exception) {
            return@withContext ResultData.Error(e.localizedMessage)
        }
    }

    /**
     * Deletes all the reminders in the db
     */
    override suspend fun deleteAllReminders(): ResultData<Int> = withContext(ioDispatcher) {
        try {
            val result = remindersDao.deleteAllReminders()
            if (result != null && result > NO_DATA_DELETED_ERROR) {
                return@withContext ResultData.Success(result)
            } else {
                return@withContext ResultData.Error(
                    message = "No Reminders have been deleted. Database is empty!",
                    statusCode = NO_DATA_DELETED_ERROR
                )
            }
        } catch (e: Exception) {
            return@withContext ResultData.Error(e.localizedMessage)
        }
    }

    override suspend fun deleteReminder(reminder: ReminderData): ResultData<Int> = withContext(ioDispatcher) {
        try {
            val result = remindersDao.deleteReminder(reminder)
            if (result != null) {
                return@withContext ResultData.Success(result)
            } else {
                return@withContext ResultData.Error(
                    message = "Delete Reminder Error!",
                    statusCode = RESULT_ERROR
                )
            }
        } catch (e: Exception) {
            return@withContext ResultData.Error(e.localizedMessage)
        }
    }

    override suspend fun updateGeofenceStatus(reminderId: Long, isGeofenceEnable: Boolean): ResultData<Int> =  withContext(ioDispatcher) {
        try {
            val result = remindersDao.updateReminder(reminderId, isGeofenceEnable)
            if (result != null) {
                return@withContext ResultData.Success(result)
            } else {
                return@withContext ResultData.Error(
                    message = "UpdateGeofenceStatus Error!",
                    statusCode = RESULT_ERROR
                )
            }
        } catch (e: Exception) {
            return@withContext ResultData.Error(e.localizedMessage)
        }
    }

    override suspend fun updateReminder(reminder: ReminderData): ResultData<Int> = withContext(ioDispatcher) {
        try {
            val result = remindersDao.updateReminder(reminder)
            if (result != null && result >= UPDATE_SUCCESS) {
                return@withContext ResultData.Success(result)
            } else {
                return@withContext ResultData.Error(
                    message = "UpdateReminder Error!",
                    statusCode = RESULT_ERROR
                )
            }
        } catch (e: Exception) {
            return@withContext ResultData.Error(e.localizedMessage)
        }
    }
}
