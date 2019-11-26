package org.firstinspires.ftc.teamcode

import org.mechdancer.ftclib.core.opmode.BaseOpMode
import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.core.structure.injector.Inject

class FinderRobot : Robot("finder", false, StoneFinder()) {

    @Inject
    lateinit var stoneFinder: StoneFinder
}

class FinderOpMode : BaseOpMode<FinderRobot>() {
    override fun initTask() {
    }

    override fun loopTask() {
        telemetry.addData("Location", robot.stoneFinder.location?.let {
            val (x, y, z, a, b, c) = it
            """
                
                x: $x
                y: $y
                z: $z
                a: $a
                b: $b
                c: $c
            """.trimIndent()
        })
    }

    override fun stopTask() {
    }

}

private operator fun DoubleArray.component6() = this[5]
