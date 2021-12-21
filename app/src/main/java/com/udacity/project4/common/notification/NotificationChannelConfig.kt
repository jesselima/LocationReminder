package com.udacity.project4.common.notification

data class NotificationChannelConfig(
    val id: String,
    val name: String,
    val importance: Int,
    val description: String,
)
