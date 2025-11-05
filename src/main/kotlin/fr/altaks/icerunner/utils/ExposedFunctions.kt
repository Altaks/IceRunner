package fr.altaks.icerunner.utils

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.log10

object ExposedFunctions {

    fun Int.length() = when (this) {
        0 -> 1
        else -> log10(abs(toDouble())).toInt() + 1
    }

    fun Double.toRadians(): Double = this / 180.0 * PI
    fun Double.toDegrees(): Double = this * 180.0 / PI
}
