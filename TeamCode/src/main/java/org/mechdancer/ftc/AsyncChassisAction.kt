//package org.mechdancer.ftc
//
//import org.mechdancer.algebra.function.vector.minus
//import org.mechdancer.algebra.implement.vector.*
//import org.mechdancer.common.ftc.Pose2D
//import org.mechdancer.common.ftc.Retimil
//import org.mechdancer.common.ftc.withTimeout
//import org.mechdancer.ftclib.algorithm.*
//import org.mechdancer.ftclib.core.opmode.async.BaseOpModeAsync
//import org.mechdancer.geometry.angle.Angle
//import org.mechdancer.geometry.angle.rotate
//import org.mechdancer.geometry.angle.toRad
//import org.mechdancer.geometry.angle.unaryMinus
//import kotlin.math.abs
//import kotlin.math.sign
//
///**
// *  [LocatableRobot] 可定位机器人
// *  常用底盘动作实现
// *
// *  继承本类（需要给定控制器参数）即可为 [BaseOpModeAsync]，
// *  在 OpMode 中对闭环控制。
// *
// */
//abstract class AsyncChassisAction<T : LocatableRobot>(
//    private val pidX: PID = PID.zero(),
//    private val pidY: PID = PID.zero(),
//    private val pidW: PID = PID.zero(),
//    /**
//     * 行进间旋转 PID 补偿
//     */
//    private val pidWC: PID = PID.zero(),
//    private val retimilX: Retimil = Retimil(Double.MAX_VALUE, .0, .0, .0),
//    private val retimilY: Retimil = Retimil(Double.MAX_VALUE, .0, .0, .0),
//    private val retimilW: Retimil = Retimil(Double.MAX_VALUE, .0, .0, .0)
//
//) : BaseOpModeAsync<T>() {
//
//    final override val initLoopMachine = LinearStateMachine()
//
//    final override val loopMachine = LinearStateMachine()
//
//    override val afterStopMachine = LinearStateMachine()
//
//    /**
//     * 底盘速度清零
//     */
//    val stopChassis: StateMember<Boolean> = {
//        robot.chassis.descartes {
//            x = .0
//            y = .0
//            w = .0
//        }
//        NEXT
//    }
//
//    /**
//     * 计数器常数
//     *
//     * 满足闭环判定条件 [counterValue] 次后跳出
//     */
//    var counterValue = 100L
//
//    /**
//     * 循环重置机器人 [timeout] 毫秒
//     */
//    fun resetWithDelay(timeout: Long = 500) =
//        { robot.reset();robot.locator.pose.p.length < 0.01 }.withTimeout(timeout)
//
//    /**
//     * 循环清零里程计 [time] 毫秒
//     *
//     * 重置机器人会清零里程计
//     */
//    fun resetLocator(time: Long = 100) =
//        { robot.locator.reset();REPEAT }.withTimeout(time)
//
//    /**
//     * 闭环任务
//     *
//     * (目标 [T] - 当前值[T]) -> 控制器 -> 输出 [O]
//     *
//     * 通常情况下，对于底盘控制任务输出类型为 [Vector3D]，三自由度速度。
//     */
//    abstract inner class TargetingTask<T, O>(tag: String) : LinearStateMachine() {
//        /**
//         *  Shadow extension [StateMachine#withTimeout]
//         */
//        fun withTimeout(timeout: Long) =
//            LinearStateMachine()
//                .add(this + Delay(timeout))
//                .add(stopChassis)
//
//        /**
//         * 误差
//         */
//        abstract val error: T
//
//        /**
//         * 目标
//         */
//        abstract val target: T
//
//        /**
//         * 输出
//         */
//        abstract val output: O
//
//        val counter = Counter(counterValue)
//
//        init {
//            add {
//                // 同步打印当前任务
//                displayTask.add {
//                    telemetry.addLine().addData(tag, target)
//                    telemetry.addLine().addData("Error", error)
//                    telemetry.addLine().addData("Output", output)
//                }
//                NEXT
//            }
//        }
//    }
//
//    private fun calculatePIDPowers(error: Pose2D) =
//        vector3DOf(pidX(error.p.x), pidY(error.p.y), pidWC(error.d.asRadian()))
//
//
//    /**
//     * PID 闭位置环移动
//     *
//     * 三自由度
//     */
//    inner class PIDMove(
//        override var target: Pose2D,
//        /**
//         * 平移误差余量
//         */
//        translationTolerance: Double = .1,
//        /**
//         * 旋转误差余量
//         */
//        rotationTolerance: Double = .1) : TargetingTask<Pose2D, Vector3D>("PIDMove") {
//
//        override var error: Pose2D = Pose2D.zero()
//
//        override var output: Vector3D = vector3DOfZero()
//
//        init {
//            add {
//                error = target minusState robot.locator.pose
//                output = calculatePIDPowers(error)
//                robot.chassis.descartes {
//                    output.x.takeIf { it != .0 || it != -.0 }?.let { x = it }
//                    output.y.takeIf { it != .0 || it != -.0 }?.let { y = it }
//                    output.z.takeIf { it != .0 || it != -.0 }?.let { w = it }
//                }
//                counter(
//                    error.p.length < translationTolerance
//                        && abs(error.d.asRadian()) < rotationTolerance
//                )
//            }
//            add(stopChassis)
//        }
//    }
//
//    /**
//     * PID 旋转
//     *
//     * 单自由度
//     */
//    inner class PIDRotation(
//        override var target: Angle,
//        /**
//         * 旋转误差余量
//         */
//        tolerance: Double = .05) : TargetingTask<Angle, Double>("PIDRotation") {
//
//        override var error: Angle = .0.toRad()
//
//        override var output: Double = .0
//
//        init {
//            add {
//                error = target.rotate(-robot.locator.pose.d)
//                output = pidW(error.asRadian())
//                robot.chassis.descartes {
//                    w = output
//                }
//                counter(abs(error.asRadian()) < tolerance)
//            }
//            add(stopChassis)
//        }
//    }
//
//    /**
//     * 开环移动
//     *
//     * 三自由度
//     */
//    inner class Move(
//        /**
//         * 底盘速度
//         */
//        powers: Vector3D,
//        /**
//         * 运动时间
//         */
//        delay: Long) : TargetingTask<Long, Vector3D>("Move") {
//
//        private val delayTask = Delay(delay)
//
//        override val target: Long = delay
//
//        override val error: Long
//            get() = delayTask.remain
//
//        override val output: Vector3D = powers
//
//
//        init {
//            add {
//                // 不清零速度
//                robot.chassis.descartes {
//                    output.x.takeIf { it != .0 || it != -.0 }?.let { x = it }
//                    output.y.takeIf { it != .0 || it != -.0 }?.let { y = it }
//                    output.z.takeIf { it != .0 || it != -.0 }?.let { w = it }
//                }
//                delayTask()
//            }
//            add(stopChassis)
//        }
//    }
//
//
//    /**
//     * 带 PID 旋转矫正的开环移动
//     *
//     * 两自由度
//     */
//    inner class MoveWithRotationCorrection(
//        /**
//         * 底盘速度
//         */
//        powers: Vector2D,
//        /**
//         * 运动时间
//         */
//        delay: Long,
//        /**
//         * 目标旋转角
//         * `null` 为开始运行任务时的初始角
//         */
//        rotation: Angle? = null)
//        : TargetingTask<Long, Vector3D>("MoveWithRotationCorrection") {
//
//        private val delayTask = Delay(delay)
//
//        override val error: Long
//            get() = delayTask.remain
//
//        override val target: Long = delay
//
//        override var output: Vector3D = vector3DOfZero()
//
//        private var targetRotation: Angle = (-233.0).toRad()
//
//        init {
//            add {
//                targetRotation = rotation ?: robot.locator.pose.d
//                NEXT
//            }
//            add {
//                val rError = targetRotation.rotate(-robot.locator.pose.d)
//                output = vector3DOf(powers.x, powers.y, pidWC(rError.asRadian()))
//
//                // 不清零速度
//                robot.chassis.descartes {
//                    output.x.takeIf { it != .0 || it != -.0 }?.let { x = it }
//                    output.y.takeIf { it != .0 || it != -.0 }?.let { y = it }
//                    output.z.takeIf { it != .0 || it != -.0 }?.let { w = it }
//                }
//                delayTask()
//            }
//            add(stopChassis)
//        }
//
//    }
//
//    private fun rangedMapperForEase(error0: Double, error: Double) =
//        abs(error0).let {
//            when {
//                it > 10.0   -> 1.0
//                it < 10 / 3 -> 10.0 / 3.0
//                else        -> 10.0 / it
//            } * error
//        }
//
//    private fun rangedMapperForEaseRotation(error0: Double, error: Double) =
//        abs(error0).let {
//            when {
//                it > 1.0      -> 1.0
//                it < 1.0 / .7 -> 1.0 / .7
//                else          -> 1.0 / it
//            } * error
//        }
//
//    /**
//     * 带 PID 旋转矫正的平滑移动
//     *
//     * 两自由度
//     */
//    inner class EaseMoveWithRotationCorrection(
//        override var target: Vector2D,
//        /**
//         * 移动误差余量
//         */
//        tolerance: Double = .3,
//        /**
//         * 目标旋转角
//         * `null` 为开始运行任务时的初始角
//         */
//        rotation: Angle? = null) : TargetingTask<Vector2D, Vector3D>("EaseMoveWithRotationCorrection") {
//
//        override var error: Vector2D = vector2DOfZero()
//
//        override var output: Vector3D = vector3DOfZero()
//
//        private var error0 = vector2DOfZero()
//
//        private var targetRotation: Angle = (-233.0).toRad()
//
//        init {
//            add {
//                targetRotation = rotation ?: robot.locator.pose.d
//                error0 = target - robot.locator.pose.p
//                NEXT
//            }
//            add {
//                error = target - robot.locator.pose.p
//                output = vector3DOf(
//                    retimilX(rangedMapperForEase(error0.x, error.x)),
//                    retimilY(rangedMapperForEase(error0.y, error.y)),
//                    pidWC(targetRotation.rotate(-robot.locator.pose.d).asRadian()))
//
//                // 不清零速度
//                robot.chassis.descartes {
//                    output.x.takeIf { it != .0 || it != -.0 }?.let { x = it }
//                    output.y.takeIf { it != .0 || it != -.0 }?.let { y = it }
//                    output.z.takeIf { it != .0 || it != -.0 }?.let { w = it }
//                }
//                counter(error0.x.sign * error.x <= tolerance && error0.y.sign * error.y <= tolerance)
//            }
//            add(stopChassis)
//
//        }
//    }
//
//    /**
//     * 平滑移动
//     *
//     * 单自由度
//     */
//    inner class EaseMove(
//        override var target: Vector2D,
//        /**
//         * 移动误差余量
//         */
//        tolerance: Double = .2
//    ) : TargetingTask<Vector2D, Vector3D>("EaseMove") {
//
//        override var error: Vector2D = vector2DOfZero()
//
//        override var output: Vector3D = vector3DOfZero()
//
//        private var error0 = vector2DOfZero()
//
//
//        init {
//            add {
//                error0 = with(robot.locator.pose.p) { vector2DOf((target.x - x), (target.y - y)) }
//                NEXT
//            }
//            add {
//                error = with(robot.locator.pose.p) { vector2DOf(target.x - x, target.y - y) }
//                output = vector3DOf(
//                    retimilX(rangedMapperForEase(error0.x, error.x)),
//                    retimilY(rangedMapperForEase(error0.y, error.y)),
//                    .0)
//
//                robot.chassis.descartes {
//                    output.x.takeIf { it != .0 || it != -.0 }?.let { x = it }
//                    output.y.takeIf { it != .0 || it != -.0 }?.let { y = it }
//                    output.z.takeIf { it != .0 || it != -.0 }?.let { w = it }
//                }
//                counter(error0.x.sign * error.x <= tolerance && error0.y.sign * error.y <= tolerance)
//            }
//            add(stopChassis)
//
//        }
//    }
//
//    /**
//     * 平滑旋转
//     */
//    inner class EaseRotation(
//        override var target: Angle,
//        /**
//         * 旋转误差余量
//         */
//        tolerance: Double = .03
//    ) : TargetingTask<Angle, Double>("EaseRotation") {
//
//        override var error: Angle = .0.toRad()
//
//        override var output: Double = .0
//
//        private var error0 = .0.toRad()
//
//        init {
//            add {
//                error0 = target.rotate(-robot.locator.pose.d)
//                NEXT
//            }
//            add {
//                error = target.rotate(-robot.locator.pose.d)
//                output = retimilW(error.asRadian())
//                robot.chassis.descartes {
//                    w = output
//                }
//                counter(error0.asRadian().sign * error.asRadian() <= tolerance)
//            }
//            add(stopChassis)
//        }
//    }
//
//
//}