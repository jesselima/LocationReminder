package com.udacity.locationreminder.features.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.udacity.locationreminder.R

class OnBoardingViewModel : ViewModel() {

    private val _onBoardingStepMutableLiveData = MutableLiveData<OnBoardingStep>()
    val onBoardingStepLiveData: LiveData<OnBoardingStep> = Transformations.map(
        _onBoardingStepMutableLiveData
    ) { it }


    fun setOnBoardingStep(index: Int) {
        _onBoardingStepMutableLiveData.value = onBoardingSteps[index]
    }

    val onBoardingSteps = listOf(
        OnBoardingStep(
            title = R.string.onboard_step_1_title,
            description = R.string.onboard_step_1_description,
            animationResId = R.raw.animation_location_pin,
        ),
        OnBoardingStep(
            title = R.string.onboard_step_2_title,
            description = R.string.onboard_step_2_description,
            animationResId = R.raw.animation_location_select,
        ),
        OnBoardingStep(
            title = R.string.onboard_step_3_title,
            description = R.string.onboard_step_3_description,
            animationResId = R.raw.animation_location_pin_alert,
        ),
    )

}