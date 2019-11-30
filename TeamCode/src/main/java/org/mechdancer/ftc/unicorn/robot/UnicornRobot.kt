package org.mechdancer.ftc.unicorn.robot

import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.core.structure.composite.chassis.Mecanum
import org.mechdancer.ftclib.core.structure.injector.Inject

class UnicornRobot : Robot("unicorn", Mecanum(enable = true), Clamp(), Slider()) {

    @Inject
    lateinit var clamp: Clamp

    @Inject
    lateinit var chassis: Mecanum

    @Inject
    lateinit var lift: Lift

    @Inject
    lateinit var slider: Slider
}