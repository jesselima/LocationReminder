package com.udacity.locationreminder.locationreminders.reminderslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.locationreminder.locationreminders.ReminderItemView
import com.udacity.locationreminder.locationreminders.data.RemindersLocalRepository
import com.udacity.locationreminder.locationreminders.data.dto.Result
import kotlinx.coroutines.launch

class RemindersListViewModel(
    private val remindersLocalRepository: RemindersLocalRepository
) : ViewModel() {

    private var _remindersList = MutableLiveData<List<ReminderItemView>>()
    val remindersList: LiveData<List<ReminderItemView>> = _remindersList

    /**
     * Get all the reminders from the DataSource and add them to the remindersList to be shown on the UI,
     * or show error if any
     */
    fun loadReminders() {
        //showLoading.value = true
        viewModelScope.launch {
            //interacting with the dataSource has to be through a coroutine
            //showLoading.postValue(false)
            when (val result = remindersLocalRepository.getReminders()) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<ReminderItemView>()
//                    dataList.addAll((result.data as List<ReminderData>).map { reminder ->
//                        //map the reminder data from the DB to the be ready to be displayed on the UI
//                        ReminderItemView(
//                            title = reminder.title,
//                            description = reminder.description,
//                            isPoi = reminder.title == null,
//                            location = reminder.location,
//                            latitude = reminder.latitude,
//                            longitude = reminder.longitude,
//                            id = reminder.id,
//                            poiId = reminder.poiId
//                        )
//                    })
                    _remindersList.value = emptyList()
                }
                is Result.Error -> null // showSnackBar.value = result.message
            }

            //check if no data has to be shown
            invalidateShowNoData()
        }
    }

    /**
     * Inform the user that there's not any data if the remindersList is empty
     */
    private fun invalidateShowNoData() {
        // showNoData.value = remindersList.value == null || remindersList.value!!.isEmpty()
    }
}