package com.udacity.locationreminder.features.addreminder.usecase

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class InputValidatorsUseCaseTest {

    private val useCase = InputValidatorsUseCase()

    @Test
    fun validateLocationName_should_return_false_when_value_is_empty() {
        // When
        val result = useCase.validateLocationName("")

        // Then
        assertFalse(result)
    }

    @Test
    fun validateLocationName_should_return_false_when_value_is_null() {
        // When
        val result = useCase.validateLocationName(null)

        // Then
        assertFalse(result)
    }

    @Test
    fun validateLocationName_should_return_false_when_value_is_valid() {
        // When
        val result = useCase.validateLocationName("Home")

        // Then
        assertTrue(result)
    }

    @Test
    fun validateTitle_should_return_false_when_value_is_empty() {
        // When
        val result = useCase.validateTitle("")

        // Then
        assertFalse(result)
    }

    @Test
    fun validateTitle_should_return_false_when_value_is_null() {
        // When
        val result = useCase.validateTitle(null)

        // Then
        assertFalse(result)
    }

    @Test
    fun validateTitle_should_return_false_when_value_is_valid() {
        // When
        val result = useCase.validateTitle("Home")

        // Then
        assertTrue(result)
    }

    @Test
    fun validateDescription_should_return_false_when_value_is_empty() {
        // When
        val result = useCase.validateDescription("")

        // Then
        assertFalse(result)
    }

    @Test
    fun validateDescription_should_return_false_when_value_is_null() {
        // When
        val result = useCase.validateDescription(null)

        // Then
        assertFalse(result)
    }

    @Test
    fun validateDescription_should_return_false_when_value_is_valid() {
        // When
        val result = useCase.validateDescription("Home")

        // Then
        assertTrue(result)
    }

    @Test
    fun validateLatLng_should_return_false_when_values_are_zero() {
        // When
        val result = useCase.validateLatLng(latitude = 0.0, longitude = 0.0)

        // Then
        assertFalse(result)
    }

    @Test
    fun validateLatLng_should_return_false_when_values_are_null() {
        // When
        val result = useCase.validateLatLng(latitude = null, longitude = null)

        // Then
        assertFalse(result)
    }

    @Test
    fun validateLatLng_should_return_false_when_values_are_valid() {
        // When
        val result = useCase.validateLatLng(latitude = 90.0, longitude = -120.0)

        // Then
        assertTrue(result)
    }
}