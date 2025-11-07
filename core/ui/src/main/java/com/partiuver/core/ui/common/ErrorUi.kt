package com.partiuver.core.ui.common

import androidx.annotation.StringRes
import com.partiuver.core.ui.R
import com.partiuver.domain.common.AppError

@StringRes
fun AppError?.messageRes(): Int = when (this) {
    AppError.NotFound -> R.string.error_not_found
    is AppError.Unknown -> R.string.error_unknown
    else -> R.string.error_unknown
}
