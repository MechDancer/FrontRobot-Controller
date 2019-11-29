package org.mechdancer.ftc.unitedbot.robot

import org.mechdancer.ftc.unitedbot.robot.UnitedBotArgs.PULL_LEFT_FASTEN
import org.mechdancer.ftc.unitedbot.robot.UnitedBotArgs.PULL_LEFT_RELEASE
import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.delegate
import org.mechdancer.ftclib.core.structure.monomeric.effector.Servo
import org.mechdancer.ftclib.core.structure.monomeric.servo
import kotlin.properties.Delegates

class Pull:AbstractStructure("pull",{
    servo("sv"){
        origin = .0
        ending = 1.0
        enable = true
    }



}){
    val sv: Servo by delegate()

    var pullState by Delegates.observable(PullState.Release) { _, _, new ->
        when (new) {
            PullState.Fasten  -> {
                sv.position=PULL_LEFT_FASTEN
            }
            PullState.Release -> {
                sv.position = PULL_LEFT_RELEASE

            }
        }
    }


    enum class PullState {
        Fasten, Release
    }
}