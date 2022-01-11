package com.udacity.project4.common.extensions

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.udacity.project4.R
import com.udacity.project4.databinding.LayoutCustomToastBinding

fun Activity.showCustomDialog(
    context: Context?,
    title: String = resources.getString(R.string.attention),
    message: String = resources.getString(R.string.something_went_wrong),
    positiveButtonText: String = resources.getString(R.string.label_ok_got_it),
    positiveButtonAction: (() -> Unit?)? = null,
    negativeButtonText: String? = null,
    negativeButtonAction: (() -> Unit?)? = null,
    neutralButtonText: String? = null,
    neutralButtonAction: (() -> Unit?)? = null,
    isCancelable: Boolean = true
) {
    context?.let {
        with(MaterialAlertDialogBuilder(it)) {
            setTitle(title)
            setMessage(message)
            setCancelable(isCancelable)
            negativeButtonText?.let { negativeText ->
                setNegativeButton(negativeText) { _, _ ->
                    negativeButtonAction?.invoke()
                }
            }
            neutralButtonText?.let { neutralText ->
                setNeutralButton(neutralText) { _, _ ->
                    neutralButtonAction?.invoke()
                }
            }

            setPositiveButton(positiveButtonText) { _, _ ->
                positiveButtonAction?.invoke()
            }.show()
        }
    }
}

fun Context.showCustomToast(
    toastType: ToastType = ToastType.SUCCESS,
    @StringRes titleResId: Int = R.string.label_success,
    titleText: String? = null,
    durationToast: Int = Toast.LENGTH_SHORT,
    offSetY: Int = 200,
    offSetX: Int = 0,
    gravity: Int = Gravity.BOTTOM
) {

    val binding = LayoutCustomToastBinding.inflate(LayoutInflater.from(this))

    binding.customText.text = titleText ?: getString(titleResId)

    val imageResId = when (toastType) {
        ToastType.ERROR -> R.drawable.ic_error
        ToastType.INFO -> R.drawable.ic_info
        ToastType.WARNING -> R.drawable.ic_warning
        else -> {
            R.drawable.ic_success
        }
    }
    binding.toastImage.setImageResource(imageResId)

    with (Toast(applicationContext)) {
        setGravity(gravity, offSetX, offSetY)
        duration = durationToast
        view = binding.root
        show()
    }
}

enum class ToastType {
    SUCCESS,
    ERROR,
    INFO,
    WARNING
}