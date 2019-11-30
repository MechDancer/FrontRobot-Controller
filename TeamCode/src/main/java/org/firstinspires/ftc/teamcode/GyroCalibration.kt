package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.robotcore.internal.system.AppUtil
import org.mechdancer.common.ftc.Gyro
import java.io.File

@Autonomous(name = "陀螺仪校准|保持静置")
class GyroCalibration : OpMode() {

    companion object {
        val file = File(AppUtil.FIRST_FOLDER, "gyro.txt").also {
            if (!it.exists())
                it.createNewFile()
        }
        val times = 100
    }

    private var startT = 0L

    private var k = DoubleArray(3)

    override fun init() {
        file.deleteOnExit()
        file.createNewFile()
        Gyro.register()
    }

    override fun start() {
        Gyro.reset()
        startT = System.currentTimeMillis()
    }

    override fun loop() {
        k.indices.forEach { index ->
            k[index] = Gyro.value[index] / (System.currentTimeMillis() - startT)
        }
        telemetry.addLine().addData("Gyro", Gyro.value.joinToString())
        telemetry.addLine().addData("K", k.joinToString())
    }

    override fun stop() {
        file.writeText(k.joinToString("\n"))
        Gyro.unregister()
    }

}