package org.mechdancer.ftc.unitedbot.robot

import org.mechdancer.common.ftc.Retimil
import org.mechdancer.common.ftc.remote.RemotePID
import org.mechdancer.ftclib.algorithm.PID
import kotlin.math.PI

object UnitedBotArgs {



    val LIFT_LEFT_POSITION_PID= RemotePID(1)

    val LIFT_RIGHT_POSITION_PID=RemotePID(2)


    const val LIFT_CLAMP_FORWARD = .0
    const val LIFT_CLAMP_BACK = .4

    const val PULL_LEFT_RELEASE=.0
    const val PULL_LEFT_FASTEN=.0
}