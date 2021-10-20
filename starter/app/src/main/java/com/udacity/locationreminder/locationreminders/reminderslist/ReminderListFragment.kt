package com.udacity.locationreminder.locationreminders.reminderslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.udacity.locationreminder.R
import com.udacity.locationreminder.base.BaseFragment
import com.udacity.locationreminder.databinding.FragmentRemindersBinding
import com.udacity.locationreminder.utils.setup
import com.udacity.locationreminder.utils.showDialog
import com.udacity.locationreminder.utils.signOut
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : BaseFragment() {

    override val _viewModel: RemindersListViewModel by viewModel()
    private lateinit var binding: FragmentRemindersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_reminders, container, false
        )
        binding.viewModel = _viewModel
        binding.refreshLayout.setOnRefreshListener { _viewModel.loadReminders() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()
        setupActionListeners()
    }

    private fun setupActionListeners() {
        binding.actionButtonAddReminder.setOnClickListener {
            navigateToAddReminder()
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    showDialog(
                        context = context,
                        title = getString(R.string.logout),
                        message = getString(R.string.logout_confirmation_message),
                        positiveButtonAction = { signOut() }
                    )
                    true
                }
                R.id.copyrights -> {
                    findNavController().navigate(R.id.navigateToCopyrights)
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        _viewModel.loadReminders()
    }

    private fun navigateToAddReminder() {
        findNavController().navigate(R.id.navigateToSaveReminder)
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {
        }
        binding.reminderssRecyclerView.setup(adapter)
    }


}
