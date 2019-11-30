package org.mechdancer.ftc.unicorn.robot

import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.delegate
import org.mechdancer.ftclib.core.structure.monomeric.effector.Motor
import org.mechdancer.ftclib.core.structure.monomeric.motor
import org.mechdancer.ftclib.util.Resettable
import kotlin.properties.Delegates

class Lift : AbstractStructure("lift", {
    motor("left") {
        direction = Motor.Direction.REVERSE
        enable = true
    }
    motor("right") {
        enable = true
    }
}), Resettable {
    private val left: Motor by delegate()
    private val right: Motor by delegate()

    var state by Delegates.observable(State.Stop) { _, _, new ->
        when (new) {
            State.Lift -> {
                left.power = 1.0
                right.power = 1.0
            }
            State.Down -> {
                left.power = -1.0
                right.power = -1.0
            }
            State.Stop -> {
                left.power = .0
                right.power = .0
            }
        }
    }

    override fun reset() {
        state = State.Stop
    }

    enum class State {
        Lift, Down, Stop
    }

}