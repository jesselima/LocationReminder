package com.udacity.project4.features.addreminder.domain.usecase

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