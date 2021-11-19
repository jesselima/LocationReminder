package com.udacity.locationreminder.common.extensions

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udacity.locationreminder.R
import com.udacity.locationreminder.sharedpresentation.BaseRecyclerViewAdapter


/**
 * Extension function to setup the RecyclerView
 */
fun <T> RecyclerView.setup(
    adapter: BaseRecyclerViewAdapter<T>
) {
    this.apply {
        layoutManager = LinearLayoutManager(this.context)
        this.adapter = adapter
    }
}

fun View.showWithFadeIn() {
    this.isVisible = true
    this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_with_interpolator))
}

fun View.hideWithFadeOut() {
    this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out_with_interpolator))
    this.visibility = View.GONE
}

fun View.showWithFadeIn(animDuration: AnimDuration = AnimDuration.TIME_500_MS) {
    this.visibility = View.VISIBLE
    this.startAnimation(
        AnimationUtils.loadAnimation(
            context,
            setAnimationDuration(animDuration)
        )
    )
}

fun setAnimationDuration(animDuration: AnimDuration): Int {
    return when(animDuration) {
        AnimDuration.TIME_1000_MS -> R.anim.fade_in_animation_1000ms
        AnimDuration.TIME_2000_MS -> R.anim.fade_in_animation_2000ms
        AnimDuration.TIME_3000_MS -> R.anim.fade_in_animation_3000ms
        AnimDuration.TIME_4000_MS -> R.anim.fade_in_animation_4000ms
        else -> R.anim.fade_in_animation_500ms
    }
}

fun View.toggleVisibility() {
    isVisible = isVisible.not()
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false) : View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun View.showAnimated(
    animationResId: Int = R.anim.fade_in,
    translationType: TranslationType = TranslationType.TRANSLATION_Y,
    animDuration: AnimDuration = AnimDuration.TIME_1000_MS,
    vararg values: Float = floatArrayOf(-50f),
) {
    this.visibility = View.VISIBLE

    ObjectAnimator.ofFloat(
        this,
        translationType.value,
        *values
    ).apply {
        duration = animDuration.value
        start()
    }
    this.startAnimation(AnimationUtils.loadAnimation(context,animationResId))
}

enum class TranslationType(val value: String) {
    TRANSLATION_Y("translationY"),
    TRANSLATION_X("translationX")
}

enum class AnimDuration(val value: Long) {
    TIME_500_MS(500L),
    TIME_1000_MS(1000L),
    TIME_2000_MS(2000L),
    TIME_3000_MS(3000L),
    TIME_4000_MS(3000L),
}