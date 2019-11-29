package org.mechdancer.ftc.unitedbot.robot

import org.mechdancer.ftc.LocatableRobot
import org.mechdancer.ftclib.core.structure.composite.chassis.Mecanum
import org.mechdancer.ftclib.core.structure.injector.Inject

class UnitedBot : LocatableRobot("UnitedBot", Mecanum(), Lift(), Arm(), Pull(), Sucker()) {

    @Inject
    lateinit var lift: Lift
    @Inject
    lateinit var arm: Arm
    @Inject
    lateinit var pull: Pull
    @Inject
    lateinit var sucker: Sucker

}