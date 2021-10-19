package com.udacity.locationreminder.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udacity.locationreminder.R
import com.udacity.locationreminder.base.BaseRecyclerViewAdapter


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

fun Fragment.setDisplayHomeAsUpEnabled(shouldDisplayActionBar: Boolean) {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            shouldDisplayActionBar
        )
    }
}

fun View.showWithFadeIn() {
    this.visibility = View.VISIBLE
    this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_with_interpolator))
}

fun View.hideWithFadeOut() {
    this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out_with_interpolator))
    this.visibility = View.GONE
}

fun View.toggleVisibility() {
    isVisible = isVisible.not()
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false) : View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}