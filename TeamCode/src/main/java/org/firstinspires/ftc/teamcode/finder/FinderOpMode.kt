package org.firstinspires.ftc.teamcode.finder

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.mechdancer.dataflow.core.linkTo
import org.mechdancer.flow.HostedOpMode

@TeleOp
class FinderOpMode : HostedOpMode<FinderRobot>() {

    override fun FinderRobot.config() {

        eventLoopDriver linkTo {
            telemetry.addData("Location", stoneFinder.idealTargetOnRobot)
        }

    }

}