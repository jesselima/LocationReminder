package com.udacity.project4.common.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard() {
    val view = activity?.findViewById(android.R.id.content) as View?
    view?.let {
        val inputsManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputsManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}