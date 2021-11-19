package com.udacity.locationreminder.features.addreminder.usecase

class InputValidatorsUseCase {

    fun validateLocationName(locationName: String?): Boolean {
        return locationName?.isNotBlank() ?: false
    }

    fun validateTitle(title: String?): Boolean {
        return title?.isNotBlank() ?: false
    }

    fun validateDescription(description: String?): Boolean {
        return description?.isNotBlank() ?: false
    }

    fun validateLatLng(latitude: Double?, longitude: Double?): Boolean {
        return latitude?.let { it != 0.0 } ?: false && longitude?.let { it != 0.0 } ?: false
    }
}