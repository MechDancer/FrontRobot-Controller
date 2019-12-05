package org.mechdancer.common.ftc.helper

import org.mechdancer.ftclib.core.structure.Structure
import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.core.structure.composite.chassis.Omnidirectinal
import org.mechdancer.ftclib.core.structure.injector.delegate

abstract class OmnidirectinalRobot(name: String,
                                   enableVoltageSensor: Boolean = false,
                                   vararg subStruct: Structure
) : Robot(name, enableVoltageSensor, *subStruct) {

    constructor(name: String, vararg subStruct: Structure)
        : this(name, false, *subStruct)

    val chassis: Omnidirectinal by delegate()

}