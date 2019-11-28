package org.mechdancer.common.ftc.remote

import org.mechdancer.common.ftc.Retimil
import java.io.DataInputStream

class RemoteRetimil(id: Int) : RemoteDelegate<Retimil>(id, 10) {

    override var core = Retimil(.0, .0, .0, .0)

    var onReset = {}

    override fun process(payload: ByteArray) {
        DataInputStream(payload.inputStream()).apply {
            if (readInt() != id) return
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
    }
}