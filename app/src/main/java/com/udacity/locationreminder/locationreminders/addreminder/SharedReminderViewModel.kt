package com.udacity.locationreminder.locationreminders.addreminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.locationreminder.locationreminders.data.RemindersLocalRepository
import com.udacity.locationreminder.locationreminders.reminderslist.ReminderItemView
import com.udacity.locationreminder.locationreminders.reminderslist.mapToDataModel
import kotlinx.coroutines.launch

class SharedReminderViewModel(
    private val remindersLocalRepository: RemindersLocalRepository
): ViewModel() {

    private var _selectedReminder = MutableLiveData<ReminderItemView?>()
    val selectedReminder: LiveData<ReminderItemView?> = _selectedReminder

    fun setSelectedReminder(reminder: ReminderItemView?) {
        clearCurrentReminder()
        _selectedReminder.value = reminder
    }

    fun saveReminder(title: String?, name: String?, description: String?) {
        _selectedReminder.value = selectedReminder.value?.copy(
            title = title, location = name, description = description
        )
        // Todo
        //  Validate input data
        //  Save reminder.
        //  Add location to GeoFences.
        viewModelScope.launch {
            selectedReminder.value?.let {
                remindersLocalRepository.saveReminder(it.mapToDataModel())
            }
        }
    }

    fun clearCurrentReminder() {
        _selectedReminder.value = null
    }
}

