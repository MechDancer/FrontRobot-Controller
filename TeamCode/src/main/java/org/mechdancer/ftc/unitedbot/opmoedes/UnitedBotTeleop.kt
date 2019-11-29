package org.mechdancer.ftc.unitedbot.opmoedes

import org.mechdancer.ftc.unitedbot.robot.UnitedBot
import org.mechdancer.ftclib.algorithm.FINISH
import org.mechdancer.ftclib.algorithm.StateMachine
import org.mechdancer.ftclib.core.opmode.async.RemoteControlOpModeAsync
import org.mechdancer.ftclib.gamepad.Gamepad

class UnitedBotTeleop : RemoteControlOpModeAsync<UnitedBot>() {

    override val afterStopMachine: StateMachine = { FINISH }
    override val initLoopMachine: StateMachine = { FINISH }


    override fun loop(master: Gamepad, helper: Gamepad) {


        robot.chassis.descartes {
            x = master.leftStick.x
            y = -master.leftStick.y
            w = -master.rightStick.y
        }

        robot.lift.left.power = helper.leftStick.x
        robot.lift.right.power = helper.leftStick.y


        robot.sucker.motor.power = if (helper.rightTrigger.isPressing()) 0.7 else .0
        robot.arm.matrix.power = helper.rightStick.x
        robot.arm.matrix.power = helper.rightStick.y

        telemetry.addData("left", robot.lift.left.position)
        telemetry.addData("right", robot.lift.right.position)

    }
}