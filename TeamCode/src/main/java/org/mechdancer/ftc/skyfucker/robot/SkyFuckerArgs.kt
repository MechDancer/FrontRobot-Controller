package org.mechdancer.ftc.skyfucker.robot

import org.mechdancer.ftclib.algorithm.PID
import kotlin.math.PI

object SkyFuckerArgs {
    const val ARM_UP_POWER = 0.7
    const val ARM_DOWN_POWER = -0.7
    const val CLAMP_FASTEN = 1.0
    const val CLAMP_RELEASE = -1.0
    const val HOOK_HOOK = .0
    const val HOOK_UNHOOK = .88
    val CHASSIS_W_PID_PROCESSION = PID(1.4, .35, .0, PI, .05)
}