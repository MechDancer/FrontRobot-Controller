package org.mechdancer.flow.config.component

internal interface HardwareConfigDsl {
    fun start()

    fun finalize() {}

    fun build(): String

}