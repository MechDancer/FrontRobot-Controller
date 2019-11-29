package org.mechdancer.ftc.skyfucker.robot

import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.core.structure.composite.chassis.Mecanum
import org.mechdancer.ftclib.core.structure.injector.Inject

class SkyFuckerRobot : Robot("skyfucker", Mecanum(), Arm(), Clamp()) {

    @Inject
    lateinit var chassis: Mecanum

    @Inject
    lateinit var arm: Arm

    @Inject
    lateinit var clamp: Clamp

}