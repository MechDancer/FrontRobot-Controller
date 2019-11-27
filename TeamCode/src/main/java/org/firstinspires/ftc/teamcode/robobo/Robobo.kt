package org.firstinspires.ftc.teamcode.robobo

import org.firstinspires.ftc.teamcode.MecanumOdometry
import org.firstinspires.ftc.teamcode.StoneFinder
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
    val stoneFinder = StoneFinder().attach()
    val odometry = MecanumOdometry().attach()

}