package com.udacity.project4.shareddata.localdatasource.models


/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with message and statusCode
 */
sealed class ResultData<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultData<T>()
    data class Error(
        val message: String?,
        val statusCode: Int? = null
    ) : ResultData<Nothing>()
}