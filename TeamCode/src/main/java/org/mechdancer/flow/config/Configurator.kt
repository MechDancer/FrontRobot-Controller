package org.mechdancer.flow.config

import com.qualcomm.ftccommon.configuration.RobotConfigFile
import com.qualcomm.ftccommon.configuration.RobotConfigFileManager
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.mechdancer.flow.config.component.RobotConfig

abstract class Configurator(private val block: RobotConfig.() -> Unit) : OpMode() {

    override fun init() {
        val raw = robotConfig(block)
        val file = RobotConfigFile.fromString(manager, raw)
        manager.writeToFile(file, false, raw)
        manager.activeConfigAndUpdateUI = file
        requestOpModeStop()
    }

    override fun loop() {

    }

    companion object {
        val manager by lazy { RobotConfigFileManager() }
    }
}