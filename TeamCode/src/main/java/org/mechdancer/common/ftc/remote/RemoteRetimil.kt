package org.mechdancer.common.ftc.remote

import org.mechdancer.common.ftc.Retimil
import org.mechdancer.remote.modules.multicast.multicastListener
import org.mechdancer.remote.presets.RemoteHub
import org.mechdancer.remote.resources.Command
import java.io.DataInputStream

class RemoteRetimil(private val id: Int, remote: RemoteHub) {
    companion object : Command {
        override val id: Byte = 10
    }

    var core = Retimil(.0, .0, .0, .0)

    var onReset = {}

    init {
        multicastListener(RemoteRetimil) { _, _, payload ->
            DataInputStream(payload.inputStream()).apply {
                if (readInt() != id) return@multicastListener
                with(core) {
                    x0 = readDouble()
                    y0 = readDouble()
                    x1 = readDouble()
                    y1 = readDouble()
                    if (readBoolean()) {
                        onReset()
                    }
                }
            }
        }.also { remote.addDependency(it) }
    }
}