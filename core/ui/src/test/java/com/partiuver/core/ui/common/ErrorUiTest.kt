package com.partiuver.core.ui.common

import com.partiuver.core.ui.R
import com.partiuver.domain.common.AppError
import org.junit.Assert.assertEquals
import org.junit.Test

class ErrorUiTest {

    @Test
    fun `NotFound mapeia para string de erro_not_found`() {
        val res = AppError.NotFound.messageRes()
        assertEquals(R.string.error_not_found, res)
    }

    @Test
    fun `Unknown mapeia para string de erro_unknown`() {
        val res = AppError.Unknown().messageRes()
        assertEquals(R.string.error_unknown, res)
    }

    @Test
    fun `null mapeia para erro_unknown por padrao`() {
        val res = (null as AppError?).messageRes()
        assertEquals(R.string.error_unknown, res)
    }
}
