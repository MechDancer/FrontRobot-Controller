package org.mechdancer.ftc.unicorn.robot

import org.mechdancer.common.ftc.Retimil
import org.mechdancer.ftclib.algorithm.PID
import kotlin.math.PI

object UnicornArgs {
    val CHASSIS_X_Retimil = Retimil(0.5, 0.15, 10.0, 0.5)
    val CHASSIS_Y_Retimil = Retimil(0.5, 0.2, 5.0, 0.6)
    val CHASSIS_W_PID_PROCESSION = PID(1.4, .35, .0, PI, .0)
}