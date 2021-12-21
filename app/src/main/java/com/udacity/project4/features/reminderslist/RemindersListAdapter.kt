package com.udacity.project4.features.reminderslist

import com.udacity.project4.R
import com.udacity.project4.sharedpresentation.BaseRecyclerViewAdapter
import com.udacity.project4.sharedpresentation.ReminderItemView

class RemindersListAdapter(
    onReminderItemClick: (selectedReminderView: ReminderItemView) -> Unit,
    viewsResIdActions: List<Pair<Int, ((selectedReminderView: ReminderItemView) -> Unit)>?>
) : BaseRecyclerViewAdapter<ReminderItemView>(onReminderItemClick, viewsResIdActions) {
    override fun getLayoutRes(viewType: Int) = R.layout.item_list_reminder
}