package com.app.network.wrapper

import com.app.network.Either
import com.app.network.ErrorModel
import com.app.network.Failure
import com.app.network.IFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

suspend inline fun <T, X> safeApiCall(
    crossinline apiCall: suspend () -> T,
    successTransform: (T) -> X
): Either<IFailure, X> {
    return try {
        val response = withContext(Dispatchers.IO) { apiCall.invoke() }
        //CustomLogger.debug("safeApiCall", "Api call successful")
        Either.Success(successTransform(response))
    } catch (e: Exception) {
       /* CustomLogger.debug(
            "safeApiCall",
            "Exception occurred while making an http call}\n${e.message}"
        )*/
        return when (e) {
            is HttpException -> {
                val errorBody = e.response()?.errorBody().toString()
                Either.Error(
                    Failure.HttpErrors(
                        ErrorModel(
                            errorBody,
                            errorMessage = e.localizedMessage
                        )
                    )
                )
            }
            is SocketTimeoutException -> {
                Either.Error(
                    Failure.Timeout(
                        ErrorModel(
                            errorMessage = e.localizedMessage
                        )
                    )
                )
            }
            is java.net.UnknownHostException -> Either.Error(
                Failure.UnknownHost(
                    ErrorModel(
                        errorMessage = e.localizedMessage
                    )
                )
            )
            is IOException -> Either.Error(
                Failure.Network(
                    ErrorModel(
                        errorMessage = e.localizedMessage
                    )
                )
            )
            else -> Either.Error(
                Failure.Other(
                    ErrorModel(
                        errorMessage = e.localizedMessage
                    )
                )
            )
        }
    }
}
