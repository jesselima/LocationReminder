package com.udacity.project4.features.onboarding

import android.os.Parcelable
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class OnBoardingStep(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @RawRes val animationResId: Int,
): Parcelable