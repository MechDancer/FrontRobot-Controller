package org.mechdancer.flow.config

import org.mechdancer.flow.config.component.HardwareConfigDsl
import org.mechdancer.flow.config.component.RobotConfig

internal fun HardwareConfigDsl.create(): String {
    start()
    finalize()
    return build()
}

/**
 * 建立机器人 XML 配置
 *
 * @param block 机器人配置 DSL 建造者
 */
fun robotConfig(block: RobotConfig.() -> Unit) = RobotConfig(block).create()
