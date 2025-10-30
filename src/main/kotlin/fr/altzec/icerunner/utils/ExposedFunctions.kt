package fr.altzec.fr.altzec.icerunner.utils

import kotlin.math.abs
import kotlin.math.log10

object ExposedFunctions {

    fun Int.length() = when (this) {
        0 -> 1
        else -> log10(abs(toDouble())).toInt() + 1
    }
}
