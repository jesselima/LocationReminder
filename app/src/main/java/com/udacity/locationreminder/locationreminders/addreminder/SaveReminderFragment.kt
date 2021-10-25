package com.udacity.locationreminder.locationreminders.addreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.udacity.locationreminder.R
import com.udacity.locationreminder.databinding.FragmentSaveReminderBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class SaveReminderFragment : Fragment() {

    private val sharedViewModel: SharedReminderViewModel by sharedViewModel()
    private lateinit var binding: FragmentSaveReminderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveReminderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        binding.buttonSelectLocation.setOnClickListener {
            findNavController().navigate(R.id.navigateToSelectLocation)
            sharedViewModel.clearCurrentReminder()
        }

        binding.saveReminderToolbar.setNavigationOnClickListener {
            findNavController().popBackStack(R.id.reminderListFragment, false)
        }

        binding.saveReminder.setOnClickListener {
            sharedViewModel.saveReminder(
                title = binding.textFieldReminderTitle.editText?.text.toString(),
                name = binding.textFieldReminderLocationName.editText?.text.toString(),
                description = binding.textFieldReminderDescription.editText?.text.toString(),
            )
        }
    }

    private fun setupObservers() {
        sharedViewModel.selectedReminder.observe(viewLifecycleOwner) { reminder ->
            if (reminder != null) {
                binding.textSelectedLocation.text = String.format(
                    Locale.getDefault(),
                    getString(R.string.lat_long_snippet),
                    reminder.latitude,
                    reminder.longitude
                )
                binding.buttonSelectLocation.text = getString(R.string.action_change_location)
                reminder.location?.let { binding.reminderLocationName.setText(reminder.location) }
            } else {
                binding.buttonSelectLocation.text = getString(R.string.action_select_location)
                binding.textSelectedLocation.text = ""
                binding.reminderLocationName.setText("")
            }
        }
    }
}
