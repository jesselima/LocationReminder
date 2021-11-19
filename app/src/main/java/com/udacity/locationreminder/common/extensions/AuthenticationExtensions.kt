package com.udacity.locationreminder.common.extensions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.udacity.locationreminder.R
import com.udacity.locationreminder.features.authentication.AuthenticationActivity

fun AppCompatActivity.validateAuthentication() {
    FirebaseAuth.getInstance().run {
        if (this.currentUser == null) {
            showCustomToast(
                titleResId = R.string.message_authentication_required,
                toastType = ToastType.WARNING
            )
            startActivity(Intent(applicationContext, AuthenticationActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
            finish()
        }
    }
}

fun Fragment.signOut() {
    activity?.let { fragmentActivity ->
        AuthUI.getInstance()
            .signOut(fragmentActivity)
            .addOnCompleteListener {
                startActivity(Intent(activity, AuthenticationActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                })
                fragmentActivity.finish()
            }
    }
}