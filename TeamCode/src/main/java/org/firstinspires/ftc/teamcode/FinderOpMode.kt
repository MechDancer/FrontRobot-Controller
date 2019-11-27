package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.mechdancer.common.ftc.structure.StoneFinder

@TeleOp(name = "Test Finder")
class FinderOpMode : OpMode() {
    private val stoneFinder = StoneFinder()
    override fun init() {
        stoneFinder.init(hardwareMap)
    }

    override fun loop() {
        stoneFinder.update()
        telemetry.addData("Target On Robot", stoneFinder.idealTargetOnRobot)

    }

    override fun stop() {
        stoneFinder.stop()
    }
}