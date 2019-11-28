package org.mechdancer.common.ftc

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix
import org.firstinspires.ftc.robotcore.external.matrices.VectorF
import org.mechdancer.algebra.core.Vector
import org.mechdancer.algebra.implement.matrix.builder.matrix
import org.mechdancer.algebra.implement.vector.toListVector

fun OpenGLMatrix.toMatrix() = matrix {
    (0 until numRows()).forEach {
        row(*this@toMatrix.getRow(it).data.toTypedArray())
    }
}

fun VectorF.toVector(): Vector = data.map { it.toDouble() }.toListVector()