package com.app.network.wrapper

import com.app.logger.CustomLogger
import com.app.network.*
import com.app.network.Util.shouldRetryHttpCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

suspend fun <T, X> safeApiCall(
    apiCall: suspend () -> T,
    successTransform: (T) -> X,
    retryPolicy: RetryPolicy = RetryPolicy()
): Either<IFailure, X> {
    try {
        val response = withContext(Dispatchers.IO) { apiCall.invoke() }
        CustomLogger.debug("safeApiCall", "Api call successful")
        return Either.Success(successTransform(response))
    } catch (e: Exception) {
        retryPolicy.reduceRetries()
        if (shouldRetryHttpCall(e) && retryPolicy.shouldTry()) {
            delay(retryPolicy.getDelay())
            CustomLogger.debug("safeApiCall", "Retry Api call")
            return safeApiCall(apiCall, successTransform, retryPolicy)
        } else {
            CustomLogger.debug(
                "safeApiCall",
                "Exception occurred while making an http call \n ${e.message}"
            )
            return when (e) {
                is HttpException -> {
                    val errorBody = e.response()?.errorBody().toString()
                    Either.Error(
                        Failure.HttpErrors(
                            ErrorModel(
                                errorCode = e.code(),
                                errorBody = errorBody,
                                errorMessage = e.localizedMessage,
                                exception = e
                            )
                        )
                    )
                }
                is SocketTimeoutException -> {
                    Either.Error(
                        Failure.Timeout(
                            ErrorModel(
                                errorMessage = e.localizedMessage,
                                exception = e
                            )
                        )
                    )
                }
                is java.net.UnknownHostException -> Either.Error(
                    Failure.UnknownHost(
                        ErrorModel(
                            errorMessage = e.localizedMessage,
                            exception = e
                        )
                    )
                )
                is IOException -> Either.Error(
                    Failure.Network(
                        ErrorModel(
                            errorMessage = e.localizedMessage,
                            exception = e
                        )
                    )
                )
                else -> Either.Error(
                    Failure.Other(
                        ErrorModel(
                            errorMessage = e.localizedMessage,
                            exception = e
                        )
                    )
                )
            }
        }
    }
}

