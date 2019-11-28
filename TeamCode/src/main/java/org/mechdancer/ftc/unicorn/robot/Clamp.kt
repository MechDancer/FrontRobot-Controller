package org.mechdancer.ftc.unicorn.robot

import org.mechdancer.ftc.unicorn.robot.UnicornArgs.CLAMP_LEFT_FASTEN
import org.mechdancer.ftc.unicorn.robot.UnicornArgs.CLAMP_LEFT_RELEASE
import org.mechdancer.ftc.unicorn.robot.UnicornArgs.CLAMP_RIGHT_FASTEN
import org.mechdancer.ftc.unicorn.robot.UnicornArgs.CLAMP_RIGHT_RELEASE
import org.mechdancer.ftc.unicorn.robot.UnicornArgs.CLAMP_ROTATE_MAX
import org.mechdancer.ftc.unicorn.robot.UnicornArgs.CLAMP_ROTATE_ZERO
import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.delegate
import org.mechdancer.ftclib.core.structure.monomeric.effector.Servo
import org.mechdancer.ftclib.core.structure.monomeric.servo
import kotlin.properties.Delegates

class Clamp : AbstractStructure("clamp", false, {
    servo("left") {
        origin = .0
        ending = 1.0
        enable = true
    }

    servo("right") {
        origin = .0
        ending = 1.0
        enable = true
    }

    servo("up") {
        origin = .0
        ending = 1.0
        enable = true
    }
}) {
    private val left: Servo by delegate()
    private val right: Servo by delegate()
    private val up: Servo by delegate()

    var clampState by Delegates.observable(ClampState.Release) { _, _, new ->
        when (new) {
            ClampState.Fasten  -> {
                left.position = CLAMP_LEFT_FASTEN
                right.position = CLAMP_RIGHT_FASTEN
            }
            ClampState.Release -> {
                left.position = CLAMP_LEFT_RELEASE
                right.position = CLAMP_RIGHT_RELEASE
            }
        }
    }

    var rotationState by Delegates.observable(RotationState.Horizontal) { _, _, new ->
        up.position = when (new) {
            RotationState.Horizontal -> CLAMP_ROTATE_ZERO
            RotationState.Vertical   -> CLAMP_ROTATE_MAX
        }
    }

    enum class ClampState {
        Fasten, Release
    }

    enum class RotationState {
        Horizontal, Vertical
    }
}