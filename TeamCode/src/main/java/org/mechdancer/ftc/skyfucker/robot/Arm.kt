package org.mechdancer.ftc.skyfucker.robot

import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.delegate
import org.mechdancer.ftclib.core.structure.monomeric.effector.Motor
import org.mechdancer.ftclib.core.structure.monomeric.motor
import org.mechdancer.ftclib.util.Resettable
import kotlin.properties.Delegates

class Arm : AbstractStructure("arm", {
    motor("left") {
        enable = true
    }
    motor("right") {
        direction=Motor.Direction.REVERSE
        enable = true
    }
}), Resettable {

    private val left: Motor by delegate()
    private val right: Motor by delegate()

    var state by Delegates.observable(LiftState.Stop) { _, _, new ->
        when (new) {
            LiftState.Lift -> {
                left.power = SkyFuckerArgs.ARM_UP_POWER
                right.power = SkyFuckerArgs.ARM_UP_POWER
            }
            LiftState.Down -> {
                left.power = SkyFuckerArgs.ARM_DOWN_POWER
                right.power = SkyFuckerArgs.ARM_DOWN_POWER
            }
            LiftState.Stop -> {
                left.power = .0
                right.power = .0
            }
        }
    }

    override fun reset() {
        state = LiftState.Stop
    }

    enum class LiftState {
        Stop, Lift, Down
    }
}