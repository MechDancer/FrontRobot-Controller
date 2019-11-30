package org.mechdancer.ftc

import org.mechdancer.common.ftc.structure.MecanumLocator
import org.mechdancer.ftclib.core.structure.Structure
import org.mechdancer.ftclib.core.structure.composite.chassis.Mecanum

/**
 * 可定位的机器人
 *
 * ** 实现该类需要手动将麦克纳姆底盘及里程计添加到子结构中 **
 */
interface LocatableRobot : Structure {

    val locator: MecanumLocator

    val chassis: Mecanum

}