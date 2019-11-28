package org.mechdancer.common.ftc.structure

import org.mechdancer.algebra.function.matrix.inverse
import org.mechdancer.algebra.function.matrix.times
import org.mechdancer.algebra.function.matrix.transpose
import org.mechdancer.algebra.function.vector.*
import org.mechdancer.algebra.implement.matrix.builder.matrix
import org.mechdancer.algebra.implement.vector.ListVector
import org.mechdancer.algebra.implement.vector.listVectorOf
import org.mechdancer.algebra.implement.vector.listVectorOfZero
import org.mechdancer.common.ftc.Pose2D
import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.Inject
import org.mechdancer.ftclib.core.structure.monomeric.MotorWithEncoder
import org.mechdancer.ftclib.core.structure.monomeric.encoder
import org.mechdancer.ftclib.core.structure.monomeric.sensor.Encoder
import org.mechdancer.ftclib.util.AutoCallable
import org.mechdancer.ftclib.util.Resettable

/**
 * 麦克纳姆底盘里程计
 *
 * 获取机器人在世界坐标系位姿
 */
class MecanumLocator(
    /**
     * 底盘几何中心到左前轮的曼哈顿距离 (m)
     */
    treadXY: Double,
    /**
     * 轮半径 (m)
     */
    private val wheelRadius: Double
) :

// 与底盘绑定到对应电机
    AbstractStructure("chassis", {
        encoder("LF") {
            cpr = MotorWithEncoder.Neverest40
            enable = true
        }
        encoder("LB") {
            cpr = MotorWithEncoder.Neverest40
            enable = true
        }
        encoder("RF") {
            cpr = MotorWithEncoder.Neverest40
            enable = true
        }
        encoder("RB") {
            cpr = MotorWithEncoder.Neverest40
            enable = true
        }

    }), Resettable, AutoCallable {

    //------------------------------------------------------------------------------------------------
    // 设备引用及状态
    //------------------------------------------------------------------------------------------------

    @Inject("LF")
    private lateinit var lf: Encoder

    @Inject("LB")
    private lateinit var lb: Encoder

    @Inject("RF")
    private lateinit var rf: Encoder

    @Inject("RB")
    private lateinit var rb: Encoder

    private var lastEncoderValues: ListVector = listVectorOfZero(4)


    /**
     * 机器人在世界坐标系位姿
     */
    var pose: Pose2D = Pose2D.zero()
        private set

    /**
     * 输出四轮编码器的读数
     */
    fun showEncoderValues() = """
        
        
        LF: ${lf.position},
        LB: ${lb.position},
        RF: ${rf.position},
        RB: ${rb.position}
    """.trimIndent()

    /**
     * 清零里程计（移动世界坐标系原点到机器人坐标系原点）
     */
    override fun reset() {
        lf.reset(.0)
        lb.reset(.0)
        rf.reset(.0)
        rb.reset(.0)
        lastEncoderValues = listVectorOfZero(4)
        pose = Pose2D.zero()
    }


    override fun run() {
        val currentEncoderValues = listVectorOf(lf.position, lb.position, rf.position, rb.position) * wheelRadius
        val (x, y, w) = solverMatrix * (currentEncoderValues - lastEncoderValues)
        lastEncoderValues = currentEncoderValues
        pose = pose plusDelta Pose2D.pose(x, y, w)
    }

    override fun toString() = "${javaClass.simpleName} | Pose: $pose"

    //------------------------------------------------------------------------------------------------
    // 解方程
    //------------------------------------------------------------------------------------------------

    private val coefficient = matrix {
        row(+1, -1, -treadXY)
        row(+1, +1, -treadXY)
        row(+1, +1, +treadXY)
        row(+1, -1, +treadXY)
    }

    private val transposed = coefficient.transpose()

    private val solverMatrix = (transposed * coefficient).inverse() * transposed

}