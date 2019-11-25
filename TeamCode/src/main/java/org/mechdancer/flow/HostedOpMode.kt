package org.mechdancer.flow

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.mechdancer.flow.RobotFactory.createRobot

abstract class HostedOpMode<T : Robot> : OpMode() {

    private val robot: T = createRobot()

    abstract fun T.config()

    final override fun init() {
        robot.init(hardwareMap)
        robot.config()
    }

    final override fun loop() {
        robot.update(gamepad1, gamepad2)
    }

    final override fun stop() {
        robot.close()
    }

}