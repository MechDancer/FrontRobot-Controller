package org.mechdancer.host.struct.effector

import org.mechdancer.dependency.Component
import org.mechdancer.host.DataBlock

/**
 * PowerOutput
 *
 * PowerOutput is an effector capable of outputting power.
 * Such as a motor or a continuous servo.
 */
interface PowerOutput : Component {

    /** Power block */
    val power: DataBlock<Double>

}
