package com.udacity.locationreminder.locationreminders.addreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.udacity.locationreminder.R
import com.udacity.locationreminder.databinding.FragmentAddReminderBinding
import com.udacity.locationreminder.locationreminders.ReminderItemView
import com.udacity.locationreminder.utils.ToastType
import com.udacity.locationreminder.utils.showCustomToast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.Locale

class AddReminderFragment : Fragment() {

    private val sharedViewModel: SharedReminderViewModel by sharedViewModel()
    private lateinit var binding: FragmentAddReminderBinding

    private var _currentReminderData: ReminderItemView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddReminderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        with(binding) {
            buttonSelectLocation.setOnClickListener {
                _currentReminderData?.locationName =
                    binding.textFieldReminderLocationName.editText?.text.toString()
                findNavController().navigate(
                    AddReminderFragmentDirections.navigateToSelectLocation(_currentReminderData)
                )
            }

            saveReminderToolbar.setNavigationOnClickListener {
                findNavController().navigate(AddReminderFragmentDirections.navigateToReminderList())
            }

            actionButtonSaveReminder.setOnClickListener {
                sharedViewModel.saveReminder(
                    title = binding.textFieldReminderTitle.editText?.text.toString(),
                    name = binding.textFieldReminderLocationName.editText?.text.toString(),
                    description = binding.textFieldReminderDescription.editText?.text.toString(),
                )
            }
        }


        // TODO - Move the input validation logic to a UseCase
        //  The fragment should only call ViewModel methods, which will use cases to validate values
        //  Then return the result to the view model, which returns it to the fragment as action/state.


        binding.reminderTitle.doOnTextChanged { text, _, _, _ ->
            if (text?.isNotBlank() == true) binding.textFieldReminderTitle.error = null
        }

        binding.reminderLocationName.doOnTextChanged { text, _, _, _ ->
            if (text?.isNotBlank() == true) binding.textFieldReminderLocationName.error = null
        }

        binding.reminderDescription.doOnTextChanged { text, _, _, _ ->
            if (text?.isNotBlank() == true) binding.textFieldReminderDescription.error = null
        }
    }

    private fun setupObservers() {
        sharedViewModel.selectedReminder.observe(viewLifecycleOwner) { reminder ->
            // Todo improve this state handling
            if (reminder != null) {
                _currentReminderData = ReminderItemView(
                    latLng =  reminder.latLng,
                    locationName = reminder.locationName
                )
                binding.textSelectedLocation.text = String.format(
                    Locale.getDefault(),
                    getString(R.string.lat_long_snippet),
                    reminder.latLng?.latitude,
                    reminder.latLng?.longitude
                )
                binding.buttonSelectLocation.text = getString(R.string.text_button_change_location)
                reminder.locationName?.let {
                    binding.reminderLocationName.setText(reminder.locationName)
                } ?: binding.reminderLocationName.setText("")
            } else {
                binding.buttonSelectLocation.text = getString(R.string.text_button_select_location)
                binding.textSelectedLocation.text = ""
                binding.reminderLocationName.setText("")
            }
        }

        sharedViewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state.isLoading
            binding.actionButtonSaveReminder.isVisible = state.isLoading.not()
        }

        sharedViewModel.action.observe(viewLifecycleOwner) { action ->
            when(action) {
                is AddReminderAction.AddReminderError ->
                    context?.showCustomToast(
                        titleResId = R.string.message_saving_reminder_error,
                        toastType = ToastType.ERROR
                    )
                is AddReminderAction.AddReminderSuccess -> {
                    context?.showCustomToast(
                        titleResId = R.string.message_saving_reminder_success,
                        toastType = ToastType.SUCCESS
                    )
                    findNavController().navigate(
                        AddReminderFragmentDirections.navigateToReminderList()
                    )
                }

                is AddReminderAction.InputErrorFieldTitle ->
                    binding.textFieldReminderTitle.error = getString(R.string.message_input_error_title)
                is AddReminderAction.InputErrorFieldLocation ->
                    binding.textFieldReminderLocationName.error = getString(R.string.message_input_error_location)
                is AddReminderAction.InputErrorFieldDescription ->
                    binding.textFieldReminderDescription.error = getString(R.string.message_input_error_description)
                AddReminderAction.InputErrorMissingLatLong ->
                    context?.showCustomToast(
                        titleResId = R.string.message_input_error_lat_long_missing,
                        toastType = ToastType.INFO,
                        durationToast = Toast.LENGTH_LONG
                    )
                AddReminderAction.ClearErrors -> {
                    binding.textFieldReminderTitle.error = null
                    binding.textFieldReminderLocationName.error = null
                    binding.textFieldReminderDescription.error = null
                }
            }
        }
    }
}
