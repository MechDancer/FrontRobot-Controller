package org.mechdancer.ftc.skyfucker.robot


import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.delegate
import org.mechdancer.ftclib.core.structure.monomeric.effector.ContinuousServo
import org.mechdancer.ftclib.core.structure.monomeric.servo
import org.mechdancer.ftclib.util.Resettable
import kotlin.properties.Delegates

class Clamp : AbstractStructure("clamp", false, {
    servo("cr") {
        origin = .0
        ending = 1.0
        enable = true
    }

}), Resettable {
    private val cr: ContinuousServo by delegate()

    var state by Delegates.observable(State.Release) { _, _, new ->
        cr.power = when (new) {
            State.Fasten -> SkyFuckerArgs.CLAMP_FASTEN
            State.Release -> SkyFuckerArgs.CLAMP_RELEASE
            State.Stop->.0
        }
    }


    override fun reset() {
        state = State.Release
    }

    enum class State {
        Fasten, Release,Stop
    }

}