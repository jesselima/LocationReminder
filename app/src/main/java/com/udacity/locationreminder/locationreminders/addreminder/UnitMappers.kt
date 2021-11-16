package com.udacity.locationreminder.locationreminders.addreminder

import java.util.concurrent.TimeUnit

fun mapInputUnitsExpirationValue(unit: String, value: Long): Long {
    return when (unit) {
        ExpirationUnits.DAYS.name -> TimeUnit.DAYS.toMillis(value)
        else -> -1
    }
}

enum class ExpirationUnits {
    DAYS,
    NEVER
}
