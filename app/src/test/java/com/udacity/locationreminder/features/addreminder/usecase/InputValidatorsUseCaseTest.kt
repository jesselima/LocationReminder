package com.udacity.locationreminder.features.addreminder.usecase

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class InputValidatorsUseCaseTest {

    private val useCase = InputValidatorsUseCase()

    @Test
    fun validateLocationName_should_return_false_when_value_is_empty() {
        // When
        val result = useCase.isLocationNameValid("")

        // Then
        assertFalse(result)
    }

    @Test
    fun validateLocationName_should_return_false_when_value_is_null() {
        // When
        val result = useCase.isLocationNameValid(null)

        // Then
        assertFalse(result)
    }

    @Test
    fun validateLocationName_should_return_false_when_value_is_valid() {
        // When
        val result = useCase.isLocationNameValid("Home")

        // Then
        assertTrue(result)
    }

    @Test
    fun validateTitle_should_return_false_when_value_is_empty() {
        // When
        val result = useCase.isTitleValid("")

        // Then
        assertFalse(result)
    }

    @Test
    fun validateTitle_should_return_false_when_value_is_null() {
        // When
        val result = useCase.isTitleValid(null)

        // Then
        assertFalse(result)
    }

    @Test
    fun validateTitle_should_return_false_when_value_is_valid() {
        // When
        val result = useCase.isTitleValid("Home")

        // Then
        assertTrue(result)
    }

    @Test
    fun validateDescription_should_return_false_when_value_is_empty() {
        // When
        val result = useCase.isDescriptionValid("")

        // Then
        assertFalse(result)
    }

    @Test
    fun validateDescription_should_return_false_when_value_is_null() {
        // When
        val result = useCase.isDescriptionValid(null)

        // Then
        assertFalse(result)
    }

    @Test
    fun validateDescription_should_return_false_when_value_is_valid() {
        // When
        val result = useCase.isDescriptionValid("Home")

        // Then
        assertTrue(result)
    }
}