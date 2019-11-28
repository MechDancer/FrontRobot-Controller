package org.firstinspires.ftc.teamcode

import org.mechdancer.algebra.function.vector.minus
import org.mechdancer.common.ftc.Pose2D
import org.mechdancer.common.ftc.Timer
import org.mechdancer.flow.algorithm.Counter
import org.mechdancer.flow.algorithm.PID
import org.mechdancer.flow.struct.preset.chassis.MecanumChassis
import org.mechdancer.geometry.angle.rotate
import org.mechdancer.geometry.angle.unaryMinus

sealed class ChassisController {

    var finished = false

    class OpenLoop(val descartes: MecanumChassis.Descartes, timeout: Long) : ChassisController() {
        val timer = Timer(timeout)
        var startup = true
    }

    abstract class CloseLoop(val target: Pose2D) : ChassisController() {
        abstract val controller: (Pose2D) -> MecanumChassis.Descartes
    }

    class SimplePID(
        target: Pose2D,
        translationTolerance: Double,
        rotationTolerance: Double,
        times: Long
    ) : CloseLoop(target) {
        val pidX = PID(.0, .0, .0, .0, .06)
        val pidY = PID(.0, .0, .0, .0, .06)
        val pidW = PID(.0, .0, .0, .0, .06)
        val counter = Counter(times)
        override val controller: (Pose2D) -> MecanumChassis.Descartes = {
            val (x, y) = it.p
            val w = it.d.asRadian()
            if (counter(x < translationTolerance && y < translationTolerance && w < rotationTolerance)) {
                finished = true
                MecanumChassis.Descartes.zero()
            } else MecanumChassis.Descartes(
                pidX(x),
                pidY(y),
                pidW(w)
            )
        }
    }

    fun transform(pose2D: Pose2D): MecanumChassis.Descartes =
        when (this) {
            is OpenLoop  -> {
                if (startup)
                    timer.reset()
                if (!timer.timeout) {
                    finished = false
                    descartes
                } else {
                    finished = true
                    MecanumChassis.Descartes.zero()
                }
            }
            is CloseLoop -> controller(Pose2D(target.p - pose2D.p, target.d.rotate(-pose2D.d)))
        }

    fun runBlocking() {
        while (!finished);
    }
}
