package org.mechdancer.common.ftc.remote

import org.mechdancer.remote.modules.multicast.multicastListener
import org.mechdancer.remote.presets.RemoteHub
import org.mechdancer.remote.resources.Command
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 远程值
 */
abstract class RemoteDelegate<T>(
    protected val id: Int,
    cmd: Byte,
    remoteHub: RemoteHub = remote
) : ReadWriteProperty<Any?, T> {

    abstract var core: T

    abstract fun process(payload: ByteArray)

    init {
        multicastListener(object : Command {
            override val id: Byte = cmd
        }) { _, _, payload ->
            process(payload)
        }.let { remoteHub.addDependency(it) }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = core
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        core = value
    }
}