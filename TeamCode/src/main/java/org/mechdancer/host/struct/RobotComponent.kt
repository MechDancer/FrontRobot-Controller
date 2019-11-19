package org.mechdancer.host.struct

import org.mechdancer.dependency.Component

/**
 * RobotComponent
 *
 * Robot component has its lifecycle: [init], [stop].
 */
interface RobotComponent : Component {

    fun init() {}

    fun stop() {}

}
