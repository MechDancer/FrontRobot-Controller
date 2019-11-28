package org.mechdancer.common.ftc.remote

import org.mechdancer.common.ftc.Retimil
import org.mechdancer.dependency.Component
import org.mechdancer.dependency.DynamicScope
import org.mechdancer.dependency.plusAssign
import org.mechdancer.ftclib.algorithm.PID
import org.mechdancer.remote.presets.RemoteHub
import org.mechdancer.remote.resources.Command
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream


fun RemoteHub.addDependency(component: Component) {
    (RemoteHub::class.java.declaredFields.find { it.name.contains("scope") }!!.also {
        it.isAccessible = true
    }[this] as DynamicScope) += component
}

fun RemoteHub.sendPID(pid: PID, id: Int, reset: Boolean = true) =
    broadcast(object : Command {
        override val id: Byte = 9
    }, ByteArrayOutputStream().let {
        DataOutputStream(it).run {
            with(pid) {
                writeInt(id)
                writeDouble(k)
                writeDouble(ki)
                writeDouble(kd)
                writeDouble(integrateArea)
                writeDouble(deadArea)
                writeBoolean(reset)
            }
            it.toByteArray()
        }
    })

fun RemoteHub.sendDouble(double: Double, id: Int) =
    broadcast(object : Command {
        override val id: Byte = 32
    }, ByteArrayOutputStream().let {
        DataOutputStream(it).run {
            writeInt(id)
            writeDouble(double)
            it.toByteArray()
        }
    })

fun RemoteHub.sendRetimil(retimil: Retimil, id: Int, reset: Boolean = true) =
    broadcast(object : Command {
        override val id: Byte = 10
    }, ByteArrayOutputStream().let {
        DataOutputStream(it).run {
            with(retimil) {
                writeInt(id)
                writeDouble(x0)
                writeDouble(y0)
                writeDouble(x1)
                writeDouble(y1)
                writeBoolean(reset)
            }
            it.toByteArray()
        }
    })