package org.mechdancer.common.ftc.helper

import org.mechdancer.common.ftc.structure.MecanumLocator
import org.mechdancer.ftclib.core.structure.Structure
import org.mechdancer.ftclib.core.structure.injector.delegate

/**
 * 可定位的机器人
 *
 * ** 实现该类需要手动将麦克纳姆底盘及里程计添加到子结构中 **
 */
abstract class LocatableRobot(
    name: String,
    enableVoltageSensor: Boolean = false,
    vararg subStruct: Structure
) : OmnidirectinalRobot(name, enableVoltageSensor, *subStruct) {

    constructor(name: String, vararg subStruct: Structure)
        : this(name, false, *subStruct)

    val locator: MecanumLocator by delegate()

}