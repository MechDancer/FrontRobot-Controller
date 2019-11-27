package org.mechdancer.ftc.unicorn.opmodes.teleop

import org.mechdancer.ftc.unicorn.robot.UnicornRobot
import org.mechdancer.ftclib.algorithm.FINISH
import org.mechdancer.ftclib.algorithm.StateMachine
import org.mechdancer.ftclib.core.opmode.async.RemoteControlOpModeAsync
import org.mechdancer.ftclib.gamepad.Gamepad

class UnicornTeleop : RemoteControlOpModeAsync<UnicornRobot>() {

    override val afterStopMachine: StateMachine = { FINISH }
    override val initLoopMachine: StateMachine = { FINISH }

    override fun loop(master: Gamepad, helper: Gamepad) {
    }
}