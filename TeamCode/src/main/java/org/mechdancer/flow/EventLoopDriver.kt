package org.mechdancer.flow

import org.mechdancer.dataflow.core.broadcast
import org.mechdancer.dataflow.core.intefaces.ISource
import org.mechdancer.dependency.UniqueComponent
import org.mechdancer.flow.struct.post

class EventLoopDriver : UniqueComponent<EventLoopDriver>(), ISource<Unit> by broadcast() {

    fun update() {
        this post Unit
    }

}