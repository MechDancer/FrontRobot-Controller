package org.mechdancer.common.ftc.remote

import org.mechdancer.ftclib.algorithm.PID
import java.io.DataInputStream

class RemotePID(id: Int) : RemoteDelegate<PID>(id, 11) {
    override var core = PID.zero()

    var onReset = {}

    override fun process(payload: ByteArray) {
        DataInputStream(payload.inputStream()).apply {
            if (readInt() != id) return
            with(core) {
                k = readDouble()
                ki = readDouble()
                kd = readDouble()
                integrateArea = readDouble()
                deadArea = readDouble()
                if (readBoolean()) {
                    core.reset()
                    onReset()
                }
            }
        }
    }
}