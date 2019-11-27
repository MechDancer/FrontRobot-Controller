package org.mechdancer.flow

import org.mechdancer.dataflow.core.broadcast
import org.mechdancer.dataflow.core.intefaces.ISource
import org.mechdancer.dependency.UniqueComponent
import org.mechdancer.flow.struct.post

class EventLoopDriver(private val delegate: ISource<Unit> = broadcast())
    : UniqueComponent<EventLoopDriver>(), ISource<Unit> by delegate {

    fun update() {
        delegate post Unit
    }

}