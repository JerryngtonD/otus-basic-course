package ru.otus.cineman.extension

import java.math.BigDecimal

fun Double.roundByDigitCount(numberOfDigitsAfterDecimalPoint: Int): Double {
    var bigDecimal = BigDecimal(this)
    bigDecimal = bigDecimal.setScale(
        numberOfDigitsAfterDecimalPoint,
        BigDecimal.ROUND_HALF_UP
    )
    return bigDecimal.toDouble()
}