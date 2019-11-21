package org.firstinspires.ftc.teamcode.robobo

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.mechdancer.dataflow.core.minus
import org.mechdancer.flow.HostedOpMode
import org.mechdancer.flow.struct.preset.chassis.MecanumChassis

@TeleOp
class RoboboRemote : HostedOpMode<Robobo>() {

    override fun Robobo.config() {

        master.updated - {
            MecanumChassis.Descartes(
                it.leftStickY,
                it.leftStickX,
                -it.rightStickX
            )
        } - chassis.descartesControl

    }

}