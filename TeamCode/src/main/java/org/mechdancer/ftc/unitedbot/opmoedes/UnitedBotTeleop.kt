package org.mechdancer.ftc.unitedbot.opmoedes

import org.mechdancer.ftc.unicorn.robot.UnicornRobot
import org.mechdancer.ftc.unitedbot.robot.UnitedBot
import org.mechdancer.ftclib.algorithm.FINISH
import org.mechdancer.ftclib.algorithm.StateMachine
import org.mechdancer.ftclib.core.opmode.async.RemoteControlOpModeAsync
import org.mechdancer.ftclib.gamepad.Gamepad

class UnitedBotTeleop : RemoteControlOpModeAsync<UnitedBot>() {

    override val afterStopMachine: StateMachine = { FINISH }
    override val initLoopMachine: StateMachine = { FINISH }



    override fun loop(master: Gamepad, helper: Gamepad) {


        robot.lift.left.power=master.leftStick.x
        robot.lift.right.power=master.leftStick.y

    }
}