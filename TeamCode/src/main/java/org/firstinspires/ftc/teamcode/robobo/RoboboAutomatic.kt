package org.firstinspires.ftc.teamcode.robobo

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.firstinspires.ftc.teamcode.ChassisController
import org.firstinspires.ftc.teamcode.ChassisController.OpenLoop
import org.mechdancer.dataflow.core.linkTo
import org.mechdancer.dataflow.core.minus
import org.mechdancer.flow.HostedOpMode
import org.mechdancer.flow.struct.preset.chassis.MecanumChassis.Descartes

class RoboboAutomatic : HostedOpMode<Robobo>() {

    private var chassisController: ChassisController = OpenLoop(Descartes.zero(), 0)

    override fun Robobo.config() {

        odometry.updated - { chassisController.transform(it) } - chassis.descartesControl

        eventLoopDriver linkTo {
            odometry.update()
            telemetry.addData("Encoders", odometry.showEncoderValues())
            telemetry.addData("Odometry", odometry.pose)
        }

    }


    override fun start() {
        GlobalScope.launch {
            chassisController = OpenLoop(Descartes(0.6, .0, .0), 1000)
            chassisController.runBlocking()
        }
    }

}