package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import org.mechdancer.common.ftc.remote.RemoteDouble
import org.mechdancer.common.ftc.remote.RemotePID

@TeleOp
class Test : OpMode() {
    lateinit var servo:Servo
    val target = RemoteDouble(1)

    override fun init() {
        servo=hardwareMap.servo["hook.servo"]
    }

    override fun loop() {
        servo.position=gamepad1.left_stick_y.toDouble()
        telemetry.addData("233",servo.position)
    }

}