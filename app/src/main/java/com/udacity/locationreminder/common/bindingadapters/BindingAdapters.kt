package com.udacity.locationreminder.common.bindingadapters

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.udacity.locationreminder.R
import com.udacity.locationreminder.common.extensions.hideWithFadeOut
import com.udacity.locationreminder.common.extensions.showWithFadeIn
import com.udacity.locationreminder.sharedpresentation.BaseRecyclerViewAdapter


object BindingAdapters {

    /**
     * Use binding adapter to set the recycler view data using livedata object
     */
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("android:liveData")
    @JvmStatic
    fun <T> setRecyclerViewData(recyclerView: RecyclerView, items: LiveData<List<T>>?) {
        items?.value?.let { itemList ->
            (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
                clear()
                addData(itemList)
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