package org.mechdancer.ftc.skyfucker.robot

import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.core.structure.composite.chassis.Mecanum
import org.mechdancer.ftclib.core.structure.injector.Inject
import org.mechdancer.ftclib.core.structure.monomeric.effector.Motor

class SkyFuckerRobot : Robot("skyfucker", Mecanum(
        enable = true,
        lfMotorDirection = Motor.Direction.REVERSE,
        lbMotorDirection = Motor.Direction.REVERSE,
        rfMotorDirection = Motor.Direction.REVERSE,
        rbMotorDirection = Motor.Direction.REVERSE
        ), Arm(), Clamp(),Hook()) {

    @Inject
    lateinit var chassis: Mecanum

    @Inject
    lateinit var arm: Arm

    @Inject
    lateinit var clamp: Clamp

    @Inject
    lateinit var hook: Hook

}