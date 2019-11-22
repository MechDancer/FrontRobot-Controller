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

    "collector"{
        motor("am")
    }


}) {

    val chassis = MecanumChassis().attach()
    val collector = Collector().attach()

}