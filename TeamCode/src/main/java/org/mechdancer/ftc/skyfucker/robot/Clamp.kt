package org.mechdancer.ftc.skyfucker.robot


import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.delegate
import org.mechdancer.ftclib.core.structure.monomeric.effector.Servo
import org.mechdancer.ftclib.core.structure.monomeric.servo
import kotlin.properties.Delegates

class Clamp : AbstractStructure("clamp", false, {
    servo("servo") {
        origin = .0
        ending = 1.0
        enable = true
    }

}) {
    private val servo: Servo by delegate()

    var state by Delegates.observable(State.Release) { _, _, new ->
        servo.position = when (new) {
            State.Fasten  -> SkyFuckerArgs.CLAMP_FASTEN
            State.Release -> SkyFuckerArgs.CLAMP_RELEASE
        }
    }


    enum class State {
        Fasten, Release
    }

}