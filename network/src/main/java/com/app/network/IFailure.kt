package com.app.network

interface IFailure {
    val errorModel: ErrorModel
}

sealed class Failure(override val errorModel: ErrorModel) : IFailure {

    data class HttpErrors(override val errorModel: ErrorModel) : Failure(errorModel)

    data class UnknownHost(override val errorModel: ErrorModel) : Failure(errorModel)

    data class Network(override val errorModel: ErrorModel) : Failure(errorModel)

    data class Timeout(override val errorModel: ErrorModel) : Failure(errorModel)

    data class Other(override val errorModel: ErrorModel) : Failure(errorModel)
}

fun getErrorModel(errorMessage: String): ErrorModel {
    return ErrorModel(errorMessage = errorMessage)
}
