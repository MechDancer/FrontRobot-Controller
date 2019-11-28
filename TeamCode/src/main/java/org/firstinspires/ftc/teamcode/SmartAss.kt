package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

@TeleOp
class SmartAss : OpMode() {

    lateinit var motor: DcMotor
    override fun init() {
        motor = hardwareMap.dcMotor["a"]
val cancellable=1
    }

    override fun loop() {
        telemetry.addData("X", motor.currentPosition)
    }

}