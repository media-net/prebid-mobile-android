package com.app.network

sealed class Either<out L, out R> where R : Any? {

    data class Error<out L>(val errorVal: L) : Either<L, Nothing>()

    data class Success<out R>(val successVal: R) : Either<Nothing, R>()

    val isError get() = this is Error<L>

    val isSuccess get() = this is Success<R>

    fun errorValue() = if (this is Error) errorVal else null

    fun successValue() = if (this is Success) successVal else null

    fun either(fnL: (L) -> Any, fnR: (R) -> Any): Any =
        when (this) {
            is Error -> fnL(errorVal)
            is Success -> fnR(successVal)
        }
}

val UnitSuccess get() = Either.Success(Unit)
