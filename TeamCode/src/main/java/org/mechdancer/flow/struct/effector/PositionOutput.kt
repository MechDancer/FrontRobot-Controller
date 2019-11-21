package org.mechdancer.flow.struct.effector

import org.mechdancer.dependency.Component
import org.mechdancer.flow.DataBlock

/**
 * Position output
 *
 * Position output is an effector capable of outputting position.
 * Such as a servo or a position-close-loop motor.
 */
interface PositionOutput : Component {

    /** Position block */
    val position: DataBlock<Double>

}
