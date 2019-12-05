package org.mechdancer.ftc.unicorn.opmodes.auto

import org.mechdancer.ftc.unicorn.robot.UnicornRobot
import org.mechdancer.ftclib.algorithm.LinearStateMachine
import org.mechdancer.ftclib.algorithm.NEXT
import org.mechdancer.ftclib.algorithm.StateMachine
import org.mechdancer.ftclib.classfilter.Naming
import org.mechdancer.ftclib.core.opmode.async.BaseOpModeAsync

@Naming("独角兽自动")
class UnicornAutomaticBase : BaseOpModeAsync<UnicornRobot>() {
    override val afterStopMachine: StateMachine = { NEXT }
    override val initLoopMachine: StateMachine = { NEXT }
    override val loopMachine: StateMachine = LinearStateMachine()

}