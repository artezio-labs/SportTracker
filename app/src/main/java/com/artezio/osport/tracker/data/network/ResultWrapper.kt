package com.artezio.osport.tracker.data.network

import retrofit2.HttpException
import java.io.IOException

// обработка результатов запроса
sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Error(val code: Int? = null, val error: Throwable? = null) : ResultWrapper<Nothing>()
    object NetworkError : ResultWrapper<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): ResultWrapper<T> {
    return try {
        ResultWrapper.Success(apiCall.invoke())
    } catch (t: Throwable) {
        when (t) {
            is IOException -> ResultWrapper.NetworkError
            is HttpException -> {
                ResultWrapper.Error(t.code(), t)
            }
            else -> ResultWrapper.Error(null, t)
        }
    }
}