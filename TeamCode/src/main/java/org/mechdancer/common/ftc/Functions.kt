package org.mechdancer.common.ftc

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix
import org.firstinspires.ftc.robotcore.external.matrices.VectorF
import org.mechdancer.algebra.core.Vector
import org.mechdancer.algebra.implement.matrix.builder.matrix
import org.mechdancer.algebra.implement.vector.toListVector
import org.mechdancer.ftclib.algorithm.Delay
import org.mechdancer.ftclib.algorithm.StateMachine
import org.mechdancer.ftclib.algorithm.plus
import org.mechdancer.ftclib.algorithm.times
import org.mechdancer.ftclib.util.SmartLogger

fun OpenGLMatrix.toMatrix() = matrix {
    (0 until numRows()).forEach {
        row(*this@toMatrix.getRow(it).data.toTypedArray())
    }
}

fun VectorF.toVector(): Vector = data.map { it.toDouble() }.toListVector()

val logger = object : SmartLogger {
    override val tag: String = "GlobalLogger"
}

fun StateMachine.withTimeout(timeout: Long): StateMachine {
    val delay = Delay(timeout)
    return this + delay
}

fun StateMachine.withTime(time: Long): StateMachine {
    val delay = Delay(time)
    return this * delay
}
