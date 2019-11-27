package org.mechdancer.common.ftc

import kotlin.math.abs
import kotlin.math.sign

class Retimil(
    var x0: Double,
    var y0: Double,
    var x1: Double,
    var y1: Double
) {

    operator fun invoke(x: Double): Double {
        return x.sign * when {
            abs(x) < x0 -> y0
            abs(x) < x1 -> (y1 - y0) / (x1 - x0) * (abs(x) - x0) + y0
            else        -> y1
        }

    }

    override fun toString(): String = "Retmil[x0: $x0 y0: $x1 y1:$y1]"

}