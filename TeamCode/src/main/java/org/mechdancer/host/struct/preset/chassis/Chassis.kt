package org.mechdancer.host.struct.preset.chassis

import org.mechdancer.host.struct.RobotComponent
import kotlin.math.abs
import kotlin.math.sign

/**
 * Chassis interface
 *
 * Actual chassis should implements this interface.
 */
interface Chassis : RobotComponent {

    /**
     * Max power of chassis
     */
    var maxPower: Double

    /**
     * Standardize power
     */
    fun DoubleArray.standardizeByMaxPower() =
        map(::abs).max()!!.let {
            if (it <= abs(maxPower))
                maxPower.sign
            else
                maxPower / it
        }.let {
            DoubleArray(size) { i ->
                this[i] * it
            }
        }


}
