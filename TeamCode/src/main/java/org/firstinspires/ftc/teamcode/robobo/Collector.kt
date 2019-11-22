package org.firstinspires.ftc.teamcode.robobo

import org.mechdancer.dataflow.core.broadcast
import org.mechdancer.dataflow.core.minus
import org.mechdancer.flow.DataBlock
import org.mechdancer.flow.struct.UniqueRobotComponent
import org.mechdancer.flow.struct.effector.Motor

class Collector : UniqueRobotComponent<Collector>() {

    private val motor: Motor by manager.find("am")

    val controller: DataBlock<State> = broadcast()

    override fun init() {
        controller - {
            when (it) {
                State.Collecting -> POWER
                State.Spiting    -> -POWER
                State.Stop       -> .0
            }
        } - motor.power
    }

    enum class State {
        Collecting, Spiting, Stop
    }

    companion object {
        const val POWER = 1.0
    }

    override fun equals(other: Any?): Boolean = false

    override fun hashCode(): Int = javaClass.hashCode()
}