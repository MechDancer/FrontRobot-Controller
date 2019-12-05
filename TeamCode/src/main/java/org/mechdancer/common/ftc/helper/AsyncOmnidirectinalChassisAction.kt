package org.mechdancer.common.ftc.helper

import org.mechdancer.algebra.implement.vector.Vector2D
import org.mechdancer.algebra.implement.vector.Vector3D
import org.mechdancer.algebra.implement.vector.vector3DOf
import org.mechdancer.algebra.implement.vector.vector3DOfZero
import org.mechdancer.common.ftc.Gyro
import org.mechdancer.common.ftc.Retimil
import org.mechdancer.ftclib.algorithm.*
import org.mechdancer.ftclib.core.opmode.async.BaseOpModeAsync
import org.mechdancer.geometry.angle.Angle
import org.mechdancer.geometry.angle.rotate
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.angle.unaryMinus
import kotlin.math.abs
import kotlin.math.sign

/**
 *  [OmnidirectinalRobot] 全向移动机器人
 *  常用底盘动作实现
 *
 *  继承本类（需要给定控制器参数）即可为 [BaseOpModeAsync]，
 *  在 OpMode 中对闭环控制。
 *
 */
abstract class AsyncOmnidirectinalChassisAction<T : OmnidirectinalRobot>(
    protected val pidW: PID,
    /**
     * 行进间旋转 PID 补偿
     */
    protected val pidWC: PID,
    protected val retimilW: Retimil
) : BaseOpModeAsync<T>() {

    final override val initLoopMachine = LinearStateMachine()

    final override val loopMachine = LinearStateMachine()

    override val afterStopMachine = LinearStateMachine()

    // 无里程计
    // 需要依靠手机陀螺仪
    // 手机竖直安装
    open val rotation
        get() = Gyro.filtered[2].toRad()

    init {

        startTask.add {
            Gyro.register()
            NEXT
        }
        stopTask.add {
            Gyro.unregister()
            NEXT
        }
    }

    /**
     * 底盘速度清零
     */
    val stopChassis: StateMember<Boolean> = {
        robot.chassis.descartes {
            x = .0
            y = .0
            w = .0
        }
        NEXT
    }

    /**
     * 计数器常数
     *
     * 满足闭环判定条件 [counterValue] 次后跳出
     */
    var counterValue = 100L

    /**
     * 闭环任务
     *
     * (目标 [T] - 当前值[T]) -> 控制器 -> 输出 [O]
     *
     * 通常情况下，对于底盘控制任务输出类型为 [Vector3D]，三自由度速度。
     */
    abstract inner class TargetingTask<T, O>(tag: String) : LinearStateMachine() {
        /**
         *  Shadow extension [StateMachine#withTimeout]
         */
        fun withTimeout(timeout: Long) =
            LinearStateMachine()
                .add(this + Delay(timeout))
                .add(stopChassis)

        /**
         * 误差
         */
        abstract val error: T

        /**
         * 目标
         */
        abstract val target: T

        /**
         * 输出
         */
        abstract val output: O

        val counter = Counter(counterValue)

        init {
            add {
                // 同步打印当前任务
                displayTask.add {
                    telemetry.addLine().addData(tag, target)
                    telemetry.addLine().addData("Error", error)
                    telemetry.addLine().addData("Output", output)
                }
                NEXT
            }
        }
    }

    /**
     * PID 旋转
     *
     * 单自由度
     */
    inner class PIDRotation(
        override var target: Angle,
        /**
         * 旋转误差余量
         */
        tolerance: Double = .05) : TargetingTask<Angle, Double>("PIDRotation") {

        override var error: Angle = .0.toRad()

        override var output: Double = .0

        init {
            add {
                error = target.rotate(-rotation)
                output = pidW(error.asRadian())
                robot.chassis.descartes {
                    w = output
                }
                counter(abs(error.asRadian()) < tolerance)
            }
            add(stopChassis)
        }
    }

    /**
     * 开环移动
     *
     * 三自由度
     */
    inner class Move(
        /**
         * 底盘速度
         */
        powers: Vector3D,
        /**
         * 运动时间
         */
        delay: Long) : TargetingTask<Long, Vector3D>("Move") {

        private val delayTask = Delay(delay)

        override val target: Long = delay

        override val error: Long
            get() = delayTask.remain

        override val output: Vector3D = powers


        init {
            add {
                // 不清零速度
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


    /**
     * 带 PID 旋转矫正的开环移动
     *
     * 两自由度
     */
    inner class MoveWithRotationCorrection(
        /**
         * 底盘速度
         */
        powers: Vector2D,
        /**
         * 运动时间
         */
        delay: Long,
        /**
         * 目标旋转角
         * `null` 为开始运行任务时的初始角
         */
        rotation: Angle? = null)
        : TargetingTask<Long, Vector3D>("MoveWithRotationCorrection") {

        private val delayTask = Delay(delay)

        override val error: Long
            get() = delayTask.remain

        override val target: Long = delay

        override var output: Vector3D = vector3DOfZero()

        private var targetRotation: Angle = (-233.0).toRad()

        init {
            add {
                targetRotation = rotation ?: this@AsyncOmnidirectinalChassisAction.rotation
                NEXT
            }
            add {
                val rError = targetRotation.rotate(-this@AsyncOmnidirectinalChassisAction.rotation)
                output = vector3DOf(powers.x, powers.y, pidWC(rError.asRadian()))

                // 不清零速度
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


    /**
     * 平滑旋转
     */
    inner class EaseRotation(
        override var target: Angle,
        /**
         * 旋转误差余量
         */
        tolerance: Double = .03
    ) : TargetingTask<Angle, Double>("EaseRotation") {

        override var error: Angle = .0.toRad()

        override var output: Double = .0

        private var error0 = .0.toRad()

        init {
            add {
                error0 = target.rotate(-this@AsyncOmnidirectinalChassisAction.rotation)
                NEXT
            }
            add {
                error = target.rotate(-this@AsyncOmnidirectinalChassisAction.rotation)
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