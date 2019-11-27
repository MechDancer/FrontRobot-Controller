package org.firstinspires.ftc.teamcode

import org.mechdancer.algebra.function.matrix.inverse
import org.mechdancer.algebra.function.matrix.times
import org.mechdancer.algebra.function.matrix.transpose
import org.mechdancer.algebra.function.vector.*
import org.mechdancer.algebra.implement.matrix.builder.matrix
import org.mechdancer.algebra.implement.vector.ListVector
import org.mechdancer.algebra.implement.vector.listVectorOf
import org.mechdancer.algebra.implement.vector.listVectorOfZero
import org.mechdancer.common.ftc.Pose2D
import org.mechdancer.dataflow.core.broadcast
import org.mechdancer.dataflow.core.intefaces.ISource
import org.mechdancer.dependency.must
import org.mechdancer.flow.struct.UniqueRobotComponent
import org.mechdancer.flow.struct.post
import org.mechdancer.flow.struct.sensor.Encoder
import org.mechdancer.flow.struct.sensor.Sensor
import java.util.concurrent.atomic.AtomicReference

class MecanumOdometry : UniqueRobotComponent<MecanumOdometry>(), Sensor<Pose2D> {

    private val lf: Encoder by manager.must("chassis.LF")
    private val lb: Encoder by manager.must("chassis.LB")
    private val rf: Encoder by manager.must("chassis.RF")
    private val rb: Encoder by manager.must("chassis.RB")


    private var lastEncoderValues: ListVector = listVectorOfZero(4)

    private val _pose = AtomicReference(Pose2D.zero())

    override val updated: ISource<Pose2D> = broadcast()

    override val name: String = javaClass.name

    val pose: Pose2D
        get() = _pose.get()

    override fun update(new: Pose2D) {
        if (_pose.getAndSet(new) != new)
            updated post new
    }

    fun showEncoderValues() = """
        
        LF: ${lf.position},
        LB: ${lb.position},
        RF: ${rf.position},
        RB: ${rb.position}
    """.trimIndent()

    override fun reset() {
        lf.reset()
        lb.reset()
        rf.reset()
        rb.reset()
        lastEncoderValues = listVectorOfZero(4)
        _pose.set(Pose2D.zero())
    }

    fun update() {
        val currentEncoderValues = listVectorOf(lf.position, lb.position, rf.position, rb.position) * TRACK
        val (x, y, w) = solverMatrix * (currentEncoderValues - lastEncoderValues)
        lastEncoderValues = currentEncoderValues
        update(pose plusDelta Pose2D.pose(x, y, w))
    }

    override fun toString() = "${javaClass.simpleName} | Pose: $pose"

    companion object {

        private const val TRACK = 1.0

        private val coefficient = matrix {
            row(+1, -1, -TREAD_XY)
            row(+1, +1, -TREAD_XY)
            row(+1, +1, +TREAD_XY)
            row(+1, -1, +TREAD_XY)
        }

        private const val TREAD_XY = 0.277

        private val transposed = coefficient.transpose()

        private val solverMatrix = (transposed * coefficient).inverse() * transposed
    }

}