package org.mechdancer.host.struct.effector

import org.mechdancer.dependency.NamedComponent
import org.mechdancer.host.checkedValue
import org.mechdancer.host.logger
import org.mechdancer.host.struct.Device

/**
 * ContinuousServo
 *
 * A kind of device realized power output using pwm.
 */
class ContinuousServo(name: String) :
    NamedComponent<ContinuousServo>(name), Device, PowerOutput, PwmOutput {

    override val power: OutputDriver<Double> = OutputDriver {
        it.checkedValue(-1.0..1.0)
            ?: logger.warn("Invalid continuous servo power: $it, from $name").run { null }
    }

    override val pwmEnable: OutputDriver<Boolean> = OutputDriver()

    override fun toString(): String = "${javaClass.simpleName}[$name]"

    override fun stop() {
        power.close()
        pwmEnable.close()
    }
}
