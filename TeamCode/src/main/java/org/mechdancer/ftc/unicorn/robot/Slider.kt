package org.mechdancer.ftc.unicorn.robot

import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.delegate
import org.mechdancer.ftclib.core.structure.monomeric.effector.Motor
import org.mechdancer.ftclib.core.structure.monomeric.motor
import kotlin.properties.Delegates

class Slider : AbstractStructure("slider", {
    motor("matrix") {
        direction = Motor.Direction.REVERSE
        enable = true
    }
}) {

    private val matrix: Motor by delegate()

    var state by Delegates.observable(State.Stop) { _, _, new ->
        matrix.power = when (new) {
            State.Expand -> .5
            State.Shrink -> -.5
            State.Stop   -> .0
        }
    }

    enum class State {
        Expand, Shrink, Stop
    }
}