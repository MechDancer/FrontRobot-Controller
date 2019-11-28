package org.firstinspires.ftc.teamcode.finder

import org.firstinspires.ftc.teamcode.StoneFinder
import org.mechdancer.flow.Robot

class FinderRobot : Robot() {

    val stoneFinder = StoneFinder().attach()

}