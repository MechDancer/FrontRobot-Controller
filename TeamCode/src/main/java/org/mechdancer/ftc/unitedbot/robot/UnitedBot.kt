package org.mechdancer.ftc.unitedbot.robot

import org.mechdancer.ftc.LocatableRobot
import org.mechdancer.ftclib.core.structure.composite.chassis.Mecanum
import org.mechdancer.ftclib.core.structure.injector.Inject

class UnitedBot:LocatableRobot("UnitedBot",Mecanum(),Lift(),Arm(),Pull()){

    @Inject
    lateinit var lift: Lift
    @Inject
    lateinit var arm: Lift
    @Inject
    lateinit var pull: Pull

}