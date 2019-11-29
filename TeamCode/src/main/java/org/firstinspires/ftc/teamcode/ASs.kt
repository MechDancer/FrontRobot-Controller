package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import org.mechdancer.common.ftc.remote.RemoteDouble

@TeleOp
class ASs : OpMode() {

    lateinit var left: Servo
    lateinit var right: Servo

    override fun init() {
        left = hardwareMap.servo["a"]
        right = hardwareMap.servo["b"]
    }

    val leftValue = RemoteDouble(1)
    val rightValue = RemoteDouble(2)
    val l by leftValue
    val r by rightValue

    override fun loop() {
        left.position = l
        right.position = r

        telemetry.addData("l", l)
        telemetry.addData("r", r)
    }

}