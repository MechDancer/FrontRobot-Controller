package org.mechdancer.common.ftc.helper

import org.mechdancer.algebra.function.vector.minus
import org.mechdancer.algebra.implement.vector.*
import org.mechdancer.common.ftc.Pose2D
import org.mechdancer.common.ftc.Retimil
import org.mechdancer.common.ftc.withTimeout
import org.mechdancer.ftclib.algorithm.NEXT
import org.mechdancer.ftclib.algorithm.PID
import org.mechdancer.ftclib.algorithm.REPEAT
import org.mechdancer.ftclib.core.opmode.async.BaseOpModeAsync
import org.mechdancer.geometry.angle.Angle
import org.mechdancer.geometry.angle.rotate
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.angle.unaryMinus
import kotlin.math.abs
import kotlin.math.sign

/**
 *  [LocatableRobot] 可定位机器人
 *  常用底盘动作实现
 *
 *  继承本类（需要给定控制器参数）即可为 [BaseOpModeAsync]，
 *  在 OpMode 中对闭环控制。
 *
 */
abstract class AsyncLocatableChassisAction<T : LocatableRobot>(
    private val pidX: PID = PID.zero(),
    private val pidY: PID = PID.zero(),
    pidW: PID = PID.zero(),
    pidWC: PID = PID.zero(),
    private val retimilX: Retimil = Retimil(Double.MAX_VALUE, .0, .0, .0),
    private val retimilY: Retimil = Retimil(Double.MAX_VALUE, .0, .0, .0),
    retimilW: Retimil = Retimil(Double.MAX_VALUE, .0, .0, .0)

) : AsyncOmnidirectinalChassisAction<T>(pidW, pidWC, retimilW) {

    override val rotation: Angle
        get() = robot.locator.pose.d

    /**
     * 循环重置机器人 [timeout] 毫秒
     */
    fun resetWithDelay(timeout: Long = 500) =
        { robot.reset();robot.locator.pose.p.length < 0.01 }.withTimeout(timeout)

    /**
     * 循环清零里程计 [time] 毫秒
     *
     * 重置机器人会清零里程计
     */
    fun resetLocator(time: Long = 100) =
        { robot.locator.reset();REPEAT }.withTimeout(time)

    private fun calculatePIDPowers(error: Pose2D) =
        vector3DOf(pidX(error.p.x), pidY(error.p.y), pidWC(error.d.asRadian()))

    /**
     * PID 闭位置环移动
     *
     * 三自由度
     */
    inner class PIDMove(
        override var target: Pose2D,
        /**
         * 平移误差余量
         */
        translationTolerance: Double = .1,
        /**
         * 旋转误差余量
         */
        rotationTolerance: Double = .1) : TargetingTask<Pose2D, Vector3D>("PIDMove") {

        override var error: Pose2D = Pose2D.zero()

        override var output: Vector3D = vector3DOfZero()

        init {
            add {
                error = target minusState robot.locator.pose
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

    /**
     * 带 PID 旋转矫正的平滑移动
     *
     * 两自由度
     */
    inner class EaseMoveWithRotationCorrection(
        override var target: Vector2D,
        /**
         * 移动误差余量
         */
        tolerance: Double = .3,
        /**
         * 目标旋转角
         * `null` 为开始运行任务时的初始角
         */
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

                // 不清零速度
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

    /**
     * 平滑移动
     *
     * 单自由度
     */
    inner class EaseMove(
        override var target: Vector2D,
        /**
         * 移动误差余量
         */
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


}