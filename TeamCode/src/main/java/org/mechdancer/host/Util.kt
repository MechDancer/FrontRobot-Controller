package org.mechdancer.host

import org.mechdancer.dataflow.core.intefaces.IFullyBlock
import org.mechdancer.dataflow.util.LinkServer
import org.mechdancer.host.struct.Device
import org.slf4j.Logger
import org.slf4j.LoggerFactory


fun <T : Comparable<T>> T.checkedValue(range: ClosedFloatingPointRange<T>) =
    takeIf { it in range }

val logger: Logger = LoggerFactory.getLogger("Robot")

fun deviceBundle(block: DeviceBundle.() -> Unit) = DeviceBundle().apply(block)

fun Robot.setupDeviceBundle(deviceBundle: DeviceBundle) {
    deviceBundle.devices.forEach {
        if (it !is Device)
            setup(it)
        else devices.add(it)
    }
}

fun Robot.setupDeviceBundle(block: DeviceBundle.() -> Unit) = setupDeviceBundle(deviceBundle(block))

fun Robot.setupDeviceBundleAndInit(deviceBundle: DeviceBundle) {
    setupDeviceBundle(deviceBundle)
    init()
}

fun Robot.setupDeviceBundleAndInit(block: DeviceBundle.() -> Unit) =
    setupDeviceBundleAndInit(deviceBundle(block))

fun breakAllConnections() = LinkServer.list.forEach { it.close() }

typealias DataBlock<T> = IFullyBlock<T, T>