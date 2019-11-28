package org.mechdancer.common.ftc.remote

import java.io.DataInputStream

class RemoteDouble(id: Int) : RemoteDelegate<Double>(id, 32) {

    override var core = .0

    var onNewData = { _: Double -> }

    override fun process(payload: ByteArray) {
        DataInputStream(payload.inputStream()).apply {
            if (readInt() != id) return
            core = readDouble()
            onNewData(core)
        }
    }

}