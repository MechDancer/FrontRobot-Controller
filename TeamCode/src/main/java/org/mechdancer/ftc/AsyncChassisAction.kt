package org.mechdancer.ftc

import org.mechdancer.algebra.function.vector.minus
import org.mechdancer.algebra.implement.vector.*
import org.mechdancer.common.ftc.Pose2D
import org.mechdancer.common.ftc.Retimil
import org.mechdancer.common.ftc.withTimeout
import org.mechdancer.ftclib.algorithm.*
import org.mechdancer.ftclib.core.opmode.async.BaseOpModeAsync
import org.mechdancer.geometry.angle.Angle
import org.mechdancer.geometry.angle.rotate
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.angle.unaryMinus
import kotlin.math.abs
import kotlin.math.sign

abstract class AsyncChassisAction(
    private val pidX: PID = PID.zero(),
    private val pidY: PID = PID.zero(),
    private val pidW: PID = PID.zero(),
    private val pidWC: PID = PID.zero(),
    private val retimilX: Retimil = Retimil(Double.MAX_VALUE, .0, .0, .0),
    private val retimilY: Retimil = Retimil(Double.MAX_VALUE, .0, .0, .0),
    private val retimilW: Retimil = Retimil(Double.MAX_VALUE, .0, .0, .0)

) : BaseOpModeAsync<LocatableRobot>() {

    final override val initLoopMachine = LinearStateMachine()

    final override val loopMachine = LinearStateMachine()

    override val afterStopMachine = LinearStateMachine()

    val stopChassis: StateMember<Boolean> = {
        robot.chassis.descartes {
            x = .0
            y = .0
            w = .0
        }
        NEXT
    }

    var counterValue = 100L

    fun resetWithDelay(timeout: Long = 500) =
        { robot.reset();robot.locator.pose.p.length < 0.01 }.withTimeout(timeout)

    fun resetLocator(time: Long = 100) =
        { robot.locator.reset();REPEAT }.withTimeout(time)

    abstract inner class TargetingTask<T, O>(tag: String) : LinearStateMachine() {
        /**
         *  Shadow extension [StateMachine#withTimeout]
         */
        fun withTimeout(timeout: Long) =
            LinearStateMachine()
                .add(this + Delay(timeout))
                .add(stopChassis)

        abstract val error: T

        abstract val target: T

        abstract val output: O

        val counter = Counter(counterValue)

        init {
            add {
                displayTask.add {
                    telemetry.addLine().addData(tag, target)
                    telemetry.addLine().addData("Error", error)
                    telemetry.addLine().addData("Output", output)
                }
                NEXT
            }
        }
    }

    private fun calculatePIDPowers(error: Pose2D) =
        vector3DOf(pidX(error.p.x), pidY(error.p.y), pidWC(error.d.asRadian()))


    inner class PIDMove(
        override var target: Pose2D,
        translationTolerance: Double = .1,
        rotationTolerance: Double = .1) : TargetingTask<Pose2D, Vector3D>("PIDMove") {

        override var error: Pose2D = Pose2D.zero()

        override var output: Vector3D = vector3DOfZero()

        init {
            add {
                error = Pose2D(target.p - robot.locator.pose.p, target.d.rotate(-robot.locator.pose.d))
                output = calculatePIDPowers(error)
                robot.chassis.descartes {
                    output.x.takeIf { it != .0 || it != -.0 }?.let { x = it }
                    output.y.takeIf { it != .0 || it != -.0 }?.let { y = it }
                    output.z.takeIf { it != .0 || it != -.0 }?.let { w = it }
                }
                counter(
                    error.p.length < translationTolerance
                        && abs(error.d.asRadian()) < rotationTolerance
                )
            }
            add(stopChassis)
        }
    }

    inner class PIDRotation(
        override var target: Angle,
        tolerance: Double = .05) : TargetingTask<Angle, Double>("PIDRotation") {

        override var error: Angle = .0.toRad()

        override var output: Double = .0

        init {
            add {
                error = target.rotate(-robot.locator.pose.d)
                output = pidW(error.asRadian())
                robot.chassis.descartes {
                    w = output
                }
                counter(abs(error.asRadian()) < tolerance)
            }
            add(stopChassis)
        }
    }

    inner class Move(powers: Vector3D, delay: Long) : TargetingTask<Long, Vector3D>("Move") {

        private val delayTask = Delay(delay)

        override val target: Long = delay

        override val error: Long
            get() = delayTask.remain

        override val output: Vector3D = powers


        init {
            add {
                robot.chassis.descartes {
                    output.x.takeIf { it != .0 || it != -.0 }?.let { x = it }
                    output.y.takeIf { it != .0 || it != -.0 }?.let { y = it }
                    output.z.takeIf { it != .0 || it != -.0 }?.let { w = it }
                }
                delayTask()
            }
            add(stopChassis)
        }
    }


    inner class MoveWithRotationCorrection(powers: Vector2D, delay: Long, rotation: Angle? = null)
        : TargetingTask<Long, Vector3D>("MoveWithRotationCorrection") {

        private val delayTask = Delay(delay)

        override val error: Long
            get() = delayTask.remain

        override val target: Long = delay

        override var output: Vector3D = vector3DOfZero()

        private var targetRotation: Angle = (-233.0).toRad()

        init {
            add {
                targetRotation = rotation ?: robot.locator.pose.d
                NEXT
            }
            add {
                val rError = targetRotation.rotate(-robot.locator.pose.d)
                output = vector3DOf(powers.x, powers.y, pidWC(rError.asRadian()))

                robot.chassis.descartes {
                    output.x.takeIf { it != .0 || it != -.0 }?.let { x = it }
                    output.y.takeIf { it != .0 || it != -.0 }?.let { y = it }
                    output.z.takeIf { it != .0 || it != -.0 }?.let { w = it }
                }
                delayTask()
            }
            add(stopChassis)
        }

    }

    private fun rangedMapperForEase(error0: Double, error: Double) =
        abs(error0).let {
            when {
                it > 10.0   -> 1.0
                it < 10 / 3 -> 10.0 / 3.0
                else        -> 10.0 / it
            } * error
        }

    private fun rangedMapperForEaseRotation(error0: Double, error: Double) =
        abs(error0).let {
            when {
                it > 1.0      -> 1.0
                it < 1.0 / .7 -> 1.0 / .7
                else          -> 1.0 / it
            } * error
        }

    inner class EaseMoveWithRotationCorrection(
        override var target: Vector2D,
        tolerance: Double = .3,
        rotation: Angle? = null) : TargetingTask<Vector2D, Vector3D>("EaseMoveWithRotationCorrection") {

        override var error: Vector2D = vector2DOfZero()

        override var output: Vector3D = vector3DOfZero()

        private var error0 = vector2DOfZero()

        private var targetRotation: Angle = (-233.0).toRad()

        init {
            add {
                targetRotation = rotation ?: robot.locator.pose.d
                error0 = target - robot.locator.pose.p
                NEXT
            }
            add {
                error = target - robot.locator.pose.p
                output = vector3DOf(
                    retimilX(rangedMapperForEase(error0.x, error.x)),
                    retimilY(rangedMapperForEase(error0.y, error.y)),
                    pidWC(targetRotation.rotate(-robot.locator.pose.d).asRadian()))

                robot.chassis.descartes {
                    output.x.takeIf { it != .0 || it != -.0 }?.let { x = it }
                    output.y.takeIf { it != .0 || it != -.0 }?.let { y = it }
                    output.z.takeIf { it != .0 || it != -.0 }?.let { w = it }
                }
                counter(error0.x.sign * error.x <= tolerance && error0.y.sign * error.y <= tolerance)
            }
            add(stopChassis)

        }
    }

    inner class EaseMove(
        override var target: Vector2D,
        tolerance: Double = .2
    ) : TargetingTask<Vector2D, Vector3D>("EaseMove") {

        override var error: Vector2D = vector2DOfZero()

        override var output: Vector3D = vector3DOfZero()

        private var error0 = vector2DOfZero()


        init {
            add {
                error0 = with(robot.locator.pose.p) { vector2DOf((target.x - x), (target.y - y)) }
                NEXT
            }
            add {
                error = with(robot.locator.pose.p) { vector2DOf(target.x - x, target.y - y) }
                output = vector3DOf(
                    retimilX(rangedMapperForEase(error0.x, error.x)),
                    retimilY(rangedMapperForEase(error0.y, error.y)),
                    .0)

                robot.chassis.descartes {
                    output.x.takeIf { it != .0 || it != -.0 }?.let { x = it }
                    output.y.takeIf { it != .0 || it != -.0 }?.let { y = it }
                    output.z.takeIf { it != .0 || it != -.0 }?.let { w = it }
                }
                counter(error0.x.sign * error.x <= tolerance && error0.y.sign * error.y <= tolerance)
            }
            add(stopChassis)

        }
    }

    inner class EaseRotation(
        override var target: Angle,
        tolerance: Double = .03
    ) : TargetingTask<Angle, Double>("EaseRotation") {

        override var error: Angle = .0.toRad()

        override var output: Double = .0

        private var error0 = .0.toRad()

        init {
            add {
                error0 = target.rotate(-robot.locator.pose.d)
                NEXT
            }
            add {
                error = target.rotate(-robot.locator.pose.d)
                output = retimilW(error.asRadian())
                robot.chassis.descartes {
                    w = output
                }
                counter(error0.asRadian().sign * error.asRadian() <= tolerance)
            }
            add(stopChassis)
        }
    }


}