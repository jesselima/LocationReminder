package com.udacity.project4.stubs

import com.udacity.project4.R
import com.udacity.project4.features.onboarding.OnBoardingStep

object Steps {
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