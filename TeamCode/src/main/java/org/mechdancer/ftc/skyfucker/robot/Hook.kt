package org.mechdancer.ftc.skyfucker.robot

import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.delegate
import org.mechdancer.ftclib.core.structure.monomeric.effector.Servo
import org.mechdancer.ftclib.core.structure.monomeric.servo
import org.mechdancer.ftclib.util.Resettable
import kotlin.properties.Delegates

class Hook : AbstractStructure("hook", {
    servo("servo") {
        origin = .0
        ending = 1.0
        enable = true
    }
}), Resettable {
    private val servo: Servo by delegate()

    var state by Delegates.observable(State.Unhook) { _, _, new ->
        servo.position = when (new) {
            State.Hook -> SkyFuckerArgs.HOOK_HOOK
            State.Unhook -> SkyFuckerArgs.HOOK_UNHOOK
        }
    }

    override fun reset() {
        state = State.Unhook
    }

    enum class State {
        Hook, Unhook
    }
}