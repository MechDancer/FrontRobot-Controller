package org.mechdancer.ftc.unicorn.opmodes.auto

import org.mechdancer.ftc.unicorn.robot.UnicornRobot
import org.mechdancer.ftclib.algorithm.LinearStateMachine
import org.mechdancer.ftclib.algorithm.NEXT
import org.mechdancer.ftclib.algorithm.StateMachine
import org.mechdancer.ftclib.core.opmode.async.BaseOpModeAsync

class UnicornAutomaticBase : BaseOpModeAsync<UnicornRobot>() {
    override val afterStopMachine: StateMachine = { NEXT }
    override val initLoopMachine: StateMachine = { NEXT }
    override val loopMachine: StateMachine = LinearStateMachine()

}