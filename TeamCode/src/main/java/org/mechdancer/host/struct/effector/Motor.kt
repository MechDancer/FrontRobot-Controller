package org.mechdancer.host.struct.effector

import org.mechdancer.dependency.NamedComponent
import org.mechdancer.host.checkedValue
import org.mechdancer.host.logger
import org.mechdancer.host.struct.Device

/**
 * Motor
 *
 * A simple device realized power output.
 */
class Motor(name: String, private val direction: Direction = Direction.FORWARD) :
    NamedComponent<Motor>(name), Device, PowerOutput {

    override val power = OutputDriver<Double> { raw ->
        raw.checkedValue(-1.0..1.0)?.let {
            it * direction.sign
        } ?: logger.warn("Invalid motor power value: $raw, from $name").run { null }
    }

    override fun toString(): String = "${javaClass.simpleName}[$name]"

    enum class Direction(val sign: Double) {
        FORWARD(1.0), REVERSED(-1.0)
    }

    override fun stop() {
        power.close()
    }

}
