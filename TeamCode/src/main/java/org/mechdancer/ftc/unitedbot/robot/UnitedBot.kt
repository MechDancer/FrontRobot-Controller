package org.mechdancer.ftc.unitedbot.robot

import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.core.structure.composite.chassis.Mecanum
import org.mechdancer.ftclib.core.structure.injector.Inject
import org.mechdancer.ftclib.core.structure.monomeric.effector.Motor

class UnitedBot : Robot("UnitedBot",
    Mecanum(
        enable = true,
        lfMotorDirection = Motor.Direction.FORWARD,
        rfMotorDirection = Motor.Direction.REVERSE,
        rbMotorDirection = Motor.Direction.REVERSE), Lift(), Arm(), Pull(), Sucker()) {

    @Inject
    lateinit var lift: Lift
    @Inject
    lateinit var arm: Arm
    @Inject
    lateinit var pull: Pull
    @Inject
    lateinit var sucker: Sucker
    @Inject
    lateinit var chassis: Mecanum
}