package org.mechdancer.ftc.skyfucker.opmodes.auto

import org.mechdancer.common.ftc.Gyro
import org.mechdancer.ftc.skyfucker.robot.SkyFuckerArgs
import org.mechdancer.ftc.skyfucker.robot.SkyFuckerRobot
import org.mechdancer.ftclib.algorithm.*
import org.mechdancer.ftclib.core.opmode.async.BaseOpModeAsync
import kotlin.math.abs

class SkyFuckerAuto : BaseOpModeAsync<SkyFuckerRobot>() {
    init {
        initTask.add {
            Gyro.register()
            NEXT
        }
        stopTask.add {
            Gyro.unregister()
            NEXT
        }
    }

    override val afterStopMachine: StateMachine = { NEXT }
    override val initLoopMachine: StateMachine = { NEXT }
    override val loopMachine: StateMachine =
            LinearStateMachine()

    /**
     * 红方
     */
    private val red = LinearStateMachine()


    //------------------------------------------------------------------------------------------------
    // 无事发生
    //------------------------------------------------------------------------------------------------
    private fun move(x: Double, y: Double, w: Double, delay: Long) =
            LinearStateMachine()
                    .add {
                        robot.chassis.descartes {
                            this.x = x
                            this.y = y
                            this.w = w
                        }
                        NEXT
                    }
                    .add(Delay(delay))
                    .add(stopChassis)

    private var rCounter = Counter(200L)
    private fun rotation(r: Double) =
            LinearStateMachine()
                    .add {
                        val error = r - Gyro.value[2]
                        robot.chassis.descartes {
                            w = SkyFuckerArgs.CHASSIS_W_PID_PROCESSION(error)
                        }
                        rCounter(abs(error) < .05)
                    }
                    .add(stopChassis)

    private val stopChassis = {
        robot.chassis.descartes {
            x = .0
            y = .0
            w = .0
        }
        NEXT
    }

}