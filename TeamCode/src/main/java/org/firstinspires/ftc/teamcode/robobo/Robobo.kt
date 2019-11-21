package org.firstinspires.ftc.teamcode.robobo

import org.mechdancer.flow.Robot
import org.mechdancer.flow.struct.preset.chassis.MecanumChassis

class Robobo : Robot({

    "chassis"{
        motor("LF")
        motor("LB")
        motor("RF")
        motor("RB")
    }


}) {

    val chassis = MecanumChassis().attach()

}