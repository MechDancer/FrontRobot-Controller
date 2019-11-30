package org.mechdancer.ftc.unitedbot.opmoedes

import org.mechdancer.ftc.unitedbot.robot.Arm
import org.mechdancer.ftc.unitedbot.robot.UnitedBot
import org.mechdancer.ftclib.core.opmode.RemoteControlOpMode
import org.mechdancer.ftclib.core.structure.monomeric.effector.Motor
import org.mechdancer.ftclib.gamepad.Gamepad

class UnitedBotTeleop : RemoteControlOpMode<UnitedBot>() {





    override fun loop(master: Gamepad, helper: Gamepad) {


        robot.chassis.descartes {
            x = master.leftStick.y
            y = -master.leftStick.x
            w = -master.rightStick.x
        }


        robot.lift.targetPower = helper.rightStick.y * 0.6
//        when {
//            helper.rightTrigger.bePressed() -> {
//                robot.lift.targetPower=0.7
//            }
//            helper.leftTrigger.bePressed()  -> {
//                robot.lift.targetPower=-0.7
//            }
//            else                            -> {
//                robot.lift.targetPower=.0
//            }
//        }

        when {
            helper.a.bePressed() -> robot.sucker.motor.power = 1.0
            helper.b.bePressed() -> robot.sucker.motor.power = -1.0
            else                 -> robot.sucker.motor.power = .0
        }

        if(helper.x.bePressed())
            robot.arm.clampState=Arm.State.Fasten

        if(helper.y.bePressed())
            robot.arm.clampState=Arm.State.Release



        robot.arm.cs.power = helper.leftStick.y * 0.6

        telemetry.addData("POSITIONrrrrrr", robot.lift.lTargetPosition.toString() + "\n" + robot.lift.lTargetPosition.toString())
        telemetry.addData("POSITIONlllllll", robot.lift.rTargetPosition.toString() + "\n" + robot.lift.rTargetPosition.toString())


        telemetry.addData("left", robot.lift.left.position)
        telemetry.addData("right", robot.lift.right.position)

        robot.lift.run()


    }

    override fun initTask() {
        robot.chassis.subStructures[0].direction = Motor.Direction.FORWARD
        robot.chassis.subStructures[2].direction = Motor.Direction.REVERSE
        robot.chassis.subStructures[3].direction = Motor.Direction.REVERSE

    }

    override fun stopTask() {

    }
}