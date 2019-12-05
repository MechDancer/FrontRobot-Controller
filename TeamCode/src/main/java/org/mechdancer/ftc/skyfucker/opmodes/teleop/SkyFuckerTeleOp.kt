package org.mechdancer.ftc.skyfucker.opmodes.teleop

import org.mechdancer.ftc.skyfucker.robot.Arm
import org.mechdancer.ftc.skyfucker.robot.Clamp
import org.mechdancer.ftc.skyfucker.robot.Hook
import org.mechdancer.ftc.skyfucker.robot.SkyFuckerRobot
import org.mechdancer.ftclib.classfilter.Naming
import org.mechdancer.ftclib.core.opmode.RemoteControlOpMode
import org.mechdancer.ftclib.gamepad.Gamepad

@Naming("日天者遥控")
class SkyFuckerTeleOp : RemoteControlOpMode<SkyFuckerRobot>() {
    override fun initTask() {

    }

    override fun loop(master: Gamepad, helper: Gamepad) {
        robot.chassis.descartes {
            x = master.leftStick.y
            y = -master.leftStick.x
            w = -master.rightStick.x
        }
        robot.arm.state=
                when{
                    master.leftBumper.bePressed()->Arm.LiftState.Lift
                    master.leftTrigger.bePressed()->Arm.LiftState.Down
                    else->Arm.LiftState.Stop
                }
        robot.clamp.state=
                when{
                    master.rightBumper.bePressed()-> Clamp.State.Release
                    master.rightTrigger.bePressed()->Clamp.State.Fasten
                    else->Clamp.State.Stop
                }
        robot.hook.state=
                when{
                    master.x.isPressing()-> Hook.State.Hook
                    master.y.isPressing()->Hook.State.Unhook
                    else->robot.hook.state
                }
    }

    override fun stopTask() {

    }

}
