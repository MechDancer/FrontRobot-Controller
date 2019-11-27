package org.mechdancer.flow.struct.sensor

import org.mechdancer.dataflow.core.broadcast
import org.mechdancer.dataflow.core.intefaces.ISource
import org.mechdancer.dependency.NamedComponent
import org.mechdancer.flow.data.EncoderData
import org.mechdancer.flow.struct.post
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.PI

/**
 * Encoder
 */
class Encoder(name: String, cpr: Double = 360.0) : NamedComponent<Encoder>(name), Sensor<EncoderData> {
    private val value = AtomicReference(EncoderData(.0, .0))

    private val ratio = 2 * PI / cpr

    private var offset = .0

    /** Current position */
    val position get() = value.get().position

    /** Current speed */
    val speed get() = value.get().speed

    override val updated: ISource<EncoderData> = broadcast()

    override fun update(new: EncoderData) {
        val transformed = EncoderData((new.position - offset) * ratio, new.speed * ratio)
        if (value.getAndSet(transformed) != transformed)
            updated post transformed
    }

    override fun toString(): String = "${javaClass.simpleName}[$name]"

    override fun reset() {
        offset = position / ratio
    }

    companion object CPR {
        const val NeveRest3_7 = 44.4
        const val NeveRest20 = 560.0
        const val Neverest40 = 1120.0
        const val NeveRest60 = 1680.0
        const val RevRobotics20HdHex = 1120.0
        const val RevRobotics40HdHex = 2240.0
        const val RevRoboticsCoreHex = 290.0
        const val Tetrix = 1440.0
        const val Matrix12V = 1478.4
        const val MatrixLegacy = 757.12
    }

}
