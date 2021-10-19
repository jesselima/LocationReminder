package com.udacity.locationreminder.utils

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.udacity.locationreminder.authentication.AuthenticationActivity

fun AppCompatActivity.validateAuthentication() {
    FirebaseAuth.getInstance().run {
        if (this.currentUser == null) {
            Toast.makeText(applicationContext, "Authentication required!", Toast.LENGTH_SHORT).show()
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