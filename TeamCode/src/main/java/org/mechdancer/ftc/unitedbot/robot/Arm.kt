package org.mechdancer.ftc.unitedbot.robot

import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.delegate
import org.mechdancer.ftclib.core.structure.monomeric.continuousServo
import org.mechdancer.ftclib.core.structure.monomeric.effector.ContinuousServo
import org.mechdancer.ftclib.core.structure.monomeric.effector.Servo
import org.mechdancer.ftclib.core.structure.monomeric.servo
import kotlin.properties.Delegates

class Arm : AbstractStructure("arm", {
    continuousServo("cs") {
        enable = true
    }

    servo("clamp") {
        origin = .0
        ending = 1.0
        enable = true
    }


}) {
    val cs: ContinuousServo by delegate()
    val clamp: Servo by delegate()

    var clampState by Delegates.observable(State.Release) { _, _, new ->
        clamp.position = when (new) {
            State.Fasten  -> UnitedBotArgs.CLAMP_FASTEN
            State.Release -> UnitedBotArgs.CLAMP_RELEASE
        }
    }


    enum class State {
        Fasten, Release
    }


}