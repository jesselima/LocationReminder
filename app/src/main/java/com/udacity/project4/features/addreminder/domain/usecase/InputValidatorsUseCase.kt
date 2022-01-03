package com.udacity.project4.features.addreminder.domain.usecase

class InputValidatorsUseCase {

    fun isLocationNameValid(locationName: String?) = locationName?.isNotBlank() ?: false

    fun isTitleValid(title: String?) = title?.isEmpty()?.not() ?: false

    fun isDescriptionValid(description: String?) = description?.isNotBlank() ?: false
}