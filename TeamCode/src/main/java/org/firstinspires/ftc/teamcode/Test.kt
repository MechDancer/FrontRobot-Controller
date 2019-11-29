package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import org.mechdancer.common.ftc.remote.RemoteDouble
import org.mechdancer.common.ftc.remote.RemotePID

@TeleOp
class Test : OpMode() {

    lateinit var left: DcMotor
    lateinit var right: DcMotor


    lateinit var LF: DcMotor
    lateinit var LB: DcMotor
    lateinit var RF: DcMotor
    lateinit var RB: DcMotor
    val remotePID = RemotePID(1)
    val matrixPID = RemotePID(2)
    lateinit var haha: CRServo
    lateinit var aaaa: DcMotor

    var targetPower = .0
    var sign = false

    val pid by remotePID
    val target = RemoteDouble(1)

    override fun init() {
//        left = hardwareMap.dcMotor["lift.left"]
//        right = hardwareMap.dcMotor["lift.right"]
//        aaaa = hardwareMap.dcMotor["arm.matrix"]
        haha = hardwareMap.crservo["arm.cs"]


//        LF = hardwareMap.dcMotor["chassis.LF"]
//        LB = hardwareMap.dcMotor["chassis.LB"]
//        RF = hardwareMap.dcMotor["chassis.RF"]
//        RB = hardwareMap.dcMotor["chassis.RB"]
        target.onNewData = {
            left.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            right.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            left.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            right.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        }
    }

    override fun loop() {
//        left.power = -remotePID.core(target.core+left.currentPosition.toDouble())
//        right.power = remotePID.core(target.core-right.currentPosition.toDouble())
//        haha.power = gamepad1.left_stick_x.toDouble()
        aaaa.power = gamepad1.left_stick_x.toDouble()
//        if (gamepad1.right_stick_x.toDouble() == .0)
//            matrixPID.core(target.core + aaaa.currentPosition)
//        else
//            aaaa.power = gamepad1.right_stick_x.toDouble()

//        LF.power = if (gamepad1.a) 1.0 else .0
//        LB.power = if (gamepad1.b) 1.0 else .0
//        RF.power = if (gamepad1.x) 1.0 else .0
//        RB.power = if (gamepad1.y) 1.0 else .0
        telemetry.addData("left", left.currentPosition)
        telemetry.addData("right", right.currentPosition)
//        telemetry.addData("matrix", aaaa.currentPosition)
        telemetry.addData("stiker", gamepad1.right_stick_x.toDouble())
        telemetry.addData("PID", remotePID.core)
    }

}