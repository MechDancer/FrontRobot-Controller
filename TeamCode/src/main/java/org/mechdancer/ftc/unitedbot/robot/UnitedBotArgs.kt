package org.mechdancer.ftc.unitedbot.robot

import org.mechdancer.ftclib.algorithm.PID

object UnitedBotArgs {


    val LIFT_LEFT_POSITION_PID = PID(0.004, .0, .0, .0, .05)
    val LIFT_RIGHT_POSITION_PID = PID(0.004, .0, .0, .0, .05)


    const val LIFT_CLAMP_FORWARD = .0
    const val LIFT_CLAMP_BACK = .4

    const val PULL_LEFT_RELEASE = .0
    const val PULL_LEFT_FASTEN = .0

    const val CLAMP_FASTEN = .0
    const val CLAMP_RELEASE = .0
}