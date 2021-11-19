package com.udacity.locationreminder.common.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    bundle: Bundle? = null,
    noinline init: Intent.() -> Unit = {},
    shouldStartForResult: Boolean = false,
    shouldFinish: Boolean = false
) {
    val intent = newIntent<T>(this)
    intent.init()
    if (shouldStartForResult) {
        startActivityForResult(intent, requestCode, bundle)
    } else {
        startActivity(intent, bundle)
    }
    if (shouldFinish) finish()
}

inline fun <reified T : Any> newIntent(context: Context) : Intent {
    return Intent(context, T::class.java)
}