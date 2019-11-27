package org.mechdancer.common.ftc

class Timer(var delay: Long = 0L) {

    private var time = -1L


    val timeout
        get() = System.currentTimeMillis() - time > delay

    fun reset() {
        time = System.currentTimeMillis()
    }

}