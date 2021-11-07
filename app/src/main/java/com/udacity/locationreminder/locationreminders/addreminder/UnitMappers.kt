package com.udacity.locationreminder.locationreminders.addreminder

import java.util.concurrent.TimeUnit

fun mapInputUnitsExpirationValue(unit: String, value: Long): Long {
    return when (unit) {
        ExpirationUnits.DAYS.name -> TimeUnit.DAYS.toMillis(value)
        ExpirationUnits.HOURS.name -> TimeUnit.HOURS.toMillis(value)
        ExpirationUnits.MINUTES.name -> TimeUnit.MINUTES.toMillis(value)
        else -> TimeUnit.DAYS.toMillis(value)
    }
}

enum class ExpirationUnits {
    DAYS,
    HOURS,
    MINUTES,
    NEVER
}
