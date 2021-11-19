package com.udacity.locationreminder.common.notification

data class NotificationChannelConfig(
    val id: String,
    val name: String,
    val importance: Int,
    val description: String,
)
