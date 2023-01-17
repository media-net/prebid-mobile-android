package com.app.network

data class ErrorModel(
    val errorCode: Int = -1,
    val errorBody: String? = null,
    val errorMessage: String? = null,
    val exception: Exception
)
