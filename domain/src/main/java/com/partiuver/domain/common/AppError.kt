package com.partiuver.domain.common

sealed class AppError {
    data object NotFound : AppError()
    data class Unknown(val cause: Throwable? = null) : AppError()
}