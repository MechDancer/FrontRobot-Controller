package org.mechdancer.ftc.unicorn.opmodes.auto

import org.mechdancer.common.ftc.remote.RemoteDouble
import org.mechdancer.common.ftc.remote.RemotePID
import org.mechdancer.common.ftc.remote.remote
import org.mechdancer.ftc.unicorn.robot.UnicornRobot
import org.mechdancer.ftclib.classfilter.Naming
import org.mechdancer.ftclib.core.opmode.BaseOpMode


@Naming("调参数")
class UnicornAutomaticTest : BaseOpMode<UnicornRobot>() {
    private val pidX = RemotePID(1, remote)
    private val target = RemoteDouble(1, remote)

    override fun initTask() {
        pidX.onReset = {
            robot.reset()
        }
    }

    override fun loopTask() {
        robot.chassis.descartes {
            x = pidX.core(target.core - robot.locator.pose.p.x)
        }
        telemetry.addData("Target", target.core)
        telemetry.addData("PID", pidX.core)
    }

    override fun stopTask() {

    }

}