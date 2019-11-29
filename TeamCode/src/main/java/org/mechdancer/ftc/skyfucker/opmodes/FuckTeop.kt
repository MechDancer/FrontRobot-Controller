package org.mechdancer.ftc.skyfucker.opmodes

import org.mechdancer.ftc.skyfucker.robot.SkyFuckerRobot
import org.mechdancer.ftclib.classfilter.Naming
import org.mechdancer.ftclib.core.opmode.RemoteControlOpMode
import org.mechdancer.ftclib.gamepad.Gamepad

@Naming("草泥马")
class FuckTeop : RemoteControlOpMode<SkyFuckerRobot>() {
    override fun initTask() {

    }

    override fun loop(master: Gamepad, helper: Gamepad) {
        robot.chassis.descartes {
            x = master.leftStick.y
            y = master.leftStick.x
            w = -master.rightStick.x
        }
    }

    override fun stopTask() {

    }

}
