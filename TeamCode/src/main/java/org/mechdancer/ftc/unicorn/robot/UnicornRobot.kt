package org.mechdancer.ftc.unicorn.robot

import org.mechdancer.ftc.LocatableRobot
import org.mechdancer.ftclib.core.structure.composite.chassis.Mecanum
import org.mechdancer.ftclib.core.structure.injector.Inject

class UnicornRobot : LocatableRobot("unicorn", Mecanum(), Clamp()) {

    @Inject
    lateinit var clamp: Clamp

}