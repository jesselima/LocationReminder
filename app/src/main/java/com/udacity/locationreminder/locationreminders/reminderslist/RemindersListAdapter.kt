package com.udacity.locationreminder.locationreminders.reminderslist

import com.udacity.locationreminder.R
import com.udacity.locationreminder.base.BaseRecyclerViewAdapter
import com.udacity.locationreminder.locationreminders.ReminderItemView

//Use data binding to show the reminder on the item
class RemindersListAdapter(callBack: (selectedReminderView: ReminderItemView) -> Unit) :
    BaseRecyclerViewAdapter<ReminderItemView>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.item_list_reminder
}