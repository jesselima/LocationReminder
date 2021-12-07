package com.udacity.locationreminder.features.addreminder.usecase

class InputValidatorsUseCase {

    fun isLocationNameValid(locationName: String?): Boolean {
        return locationName?.isNotBlank() ?: false
    }

    fun isTitleValid(title: String?): Boolean {
        return title?.isNotBlank() ?: false
    }

    fun isDescriptionValid(description: String?): Boolean {
        return description?.isNotBlank() ?: false
    }
}