package org.firstinspires.ftc.teamcode.robobo

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.mechdancer.dataflow.core.minus
import org.mechdancer.flow.HostedOpMode
import org.mechdancer.flow.struct.preset.chassis.MecanumChassis

@TeleOp
@Disabled
class RoboboRemote : HostedOpMode<Robobo>() {

    override fun Robobo.config() {

        master.updated - {
            MecanumChassis.Descartes(
                it.leftStickY,
                it.leftStickX,
                -it.rightStickX
            )
        } - chassis.descartesControl


        with(master) {
            leftTrigger.pressing - { Collector.State.Collecting } - collector.controller
            leftTrigger.pressing - { Collector.State.Collecting } - collector.controller
            rightTrigger.releasing - { Collector.State.Spiting } - collector.controller
            rightTrigger.releasing - { Collector.State.Stop } - collector.controller
        }
    }

}