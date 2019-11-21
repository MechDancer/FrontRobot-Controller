package org.mechdancer.flow.struct.effector

import org.mechdancer.dependency.NamedComponent
import org.mechdancer.flow.algorithm.Lens
import org.mechdancer.flow.checkedValue
import org.mechdancer.flow.logger
import org.mechdancer.flow.struct.Device

/**
 * Servo
 *
 * A simple device realized position output using pwm.
 */
class Servo(name: String, range: ClosedFloatingPointRange<Double>) : NamedComponent<Servo>(name),
                                                                     Device, PositionOutput, PwmOutput {

    private val mapper = Lens(-1.0, 1.0, range.start, range.endInclusive)

    override val position: OutputDriver<Double> = OutputDriver { raw ->
        raw.checkedValue(range)?.let {
            mapper(it)
        } ?: logger.warn("Invalid servo position: $raw, from $name").run { null }
    }

    override val pwmEnable: OutputDriver<Boolean> = OutputDriver()

    override fun toString(): String = "${javaClass.simpleName}[$name]"

}