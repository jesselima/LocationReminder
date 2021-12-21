package com.udacity.project4.common.bindingadapters

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project4.R
import com.udacity.project4.common.extensions.hideWithFadeOut
import com.udacity.project4.common.extensions.showWithFadeIn
import com.udacity.project4.sharedpresentation.BaseRecyclerViewAdapter


object BindingAdapters {

    /**
     * Use binding adapter to set the recycler view data using livedata object
     */
    @SuppressLint("NotifyDataSetChanged")
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("android:recyclerListItems")
    @JvmStatic
    fun <T> setRecyclerViewData(recyclerView: RecyclerView, items: List<T>?) {
        items?.let { list ->
            (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
                clear()
                addData(list)
                notifyDataSetChanged()
            }
        }
    }

    /**
     * Use this binding adapter to show and hide the views using boolean variables
     */
    @BindingAdapter("android:fadeVisible")
    @JvmStatic
    fun setFadeVisible(view: View, visible: Boolean? = true) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = if (visible == true) View.VISIBLE else View.GONE
        } else {
            view.animate().cancel()
            if (visible == true) {
                if (view.visibility == View.GONE)
                    view.showWithFadeIn()
            } else {
                if (view.visibility == View.VISIBLE)
                    view.hideWithFadeOut()
            }
        }
    }

    @BindingAdapter("android:loadingStatus")
    @JvmStatic
    fun setLoadingStatus(view: View, isLoading: Boolean? = false) {
        if (isLoading == true) {
            view.showWithFadeIn()
        } else {
            view.hideWithFadeOut()
        }
    }

    @JvmStatic
    @BindingAdapter("android:setImageMapResource")
    fun setImageMapResource(imageView: ImageView, isGeofenceEnable: Boolean) {
        if(isGeofenceEnable) {
            imageView.setImageResource(R.drawable.ic_map_alert_bg_transparent_enable)
        } else {
            imageView.setImageResource(R.drawable.ic_map_alert_bg_transparent_disable)
        }
    }
}