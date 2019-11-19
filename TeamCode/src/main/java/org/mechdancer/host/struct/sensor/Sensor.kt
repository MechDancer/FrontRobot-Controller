package org.mechdancer.host.struct.sensor

import org.mechdancer.dataflow.core.intefaces.ISource
import org.mechdancer.host.struct.Device

/**
 * Sensor
 *
 * A sensor is a device as well.
 * It has a capability that accept a value and broadcast to subscribers.
 */
interface Sensor<T> : Device {
    /** Value updated event */
    val updated: ISource<T>

    /** Invoke to update the value */
    fun update(new: T)
}
