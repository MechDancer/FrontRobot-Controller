package org.mechdancer.ftc.unicorn.opmodes.teleop

import org.mechdancer.ftc.unicorn.robot.Clamp
import org.mechdancer.ftc.unicorn.robot.Lift
import org.mechdancer.ftc.unicorn.robot.Slider
import org.mechdancer.ftc.unicorn.robot.UnicornRobot
import org.mechdancer.ftclib.algorithm.FINISH
import org.mechdancer.ftclib.algorithm.StateMachine
import org.mechdancer.ftclib.core.opmode.async.RemoteControlOpModeAsync
import org.mechdancer.ftclib.gamepad.Gamepad

class UnicornTeleop : RemoteControlOpModeAsync<UnicornRobot>() {

    override val afterStopMachine: StateMachine = { FINISH }
    override val initLoopMachine: StateMachine = { FINISH }

    override fun loop(master: Gamepad, helper: Gamepad) {
        robot.chassis.descartes {
            x = master.leftStick.y
            y = -master.leftStick.x
            w = -master.rightStick.x
        }
        if (helper.rightTrigger.isPressing())
            robot.clamp.clampState = Clamp.ClampState.Fasten
        if (helper.rightBumper.isPressing())
            robot.clamp.clampState = Clamp.ClampState.Release
        robot.lift.state = when {
            helper.rightStick.y > .0  -> Lift.State.Lift
            helper.rightStick.y < .0  -> Lift.State.Down
            helper.rightStick.y == .0 -> Lift.State.Stop
            else                      -> robot.lift.state
        }
        robot.slider.state = when {
            helper.leftStick.y > .0  -> Slider.State.Expand
            helper.leftStick.y < .0  -> Slider.State.Shrink
            helper.leftStick.y == .0 -> Slider.State.Stop
            else                     -> robot.slider.state
        }
    }
}