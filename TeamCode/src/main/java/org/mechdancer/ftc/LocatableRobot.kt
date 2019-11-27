package org.mechdancer.ftc

import org.mechdancer.common.ftc.structure.MecanumLocator
import org.mechdancer.ftclib.core.structure.Structure
import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.core.structure.composite.chassis.Mecanum
import org.mechdancer.ftclib.core.structure.injector.Inject

/**
 * 可定位的机器人
 *
 * ** 实现该类需要手动将麦克纳姆底盘及里程计添加到子结构中 **
 */
abstract class LocatableRobot(name: String, vararg struct: Structure)
    : Robot(name, false, *struct) {

    @Inject(name = "chassis")
    lateinit var locator: MecanumLocator

    @Inject
    lateinit var chassis: Mecanum

}