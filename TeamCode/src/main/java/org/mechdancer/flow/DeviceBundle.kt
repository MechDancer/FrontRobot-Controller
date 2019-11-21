package org.mechdancer.flow

import org.mechdancer.dependency.NamedComponent
import org.mechdancer.flow.struct.Device
import org.mechdancer.flow.struct.effector.ContinuousServo
import org.mechdancer.flow.struct.effector.Motor
import org.mechdancer.flow.struct.effector.Servo
import org.mechdancer.flow.struct.preset.MotorWithEncoder
import org.mechdancer.flow.struct.sensor.ColorSensor
import org.mechdancer.flow.struct.sensor.Encoder
import org.mechdancer.flow.struct.sensor.Gyro
import org.mechdancer.flow.struct.sensor.TouchSensor

open class DeviceBundle {


    @PublishedApi
    internal val devices = mutableListOf<NamedComponent<*>>()

    private fun <T : NamedComponent<T>> T.add() = apply {
        devices += this
    }

    inner class NamedScope(private val prefix: String) {

        private fun named(name: String) = "$prefix.$name"

        fun motor(name: String, direction: Motor.Direction = Motor.Direction.FORWARD) =
            this@DeviceBundle.motor(named(name), direction)

        fun servo(name: String, range: ClosedFloatingPointRange<Double>) =
            this@DeviceBundle.servo(named(name), range)

        fun continuousServo(name: String) =
            this@DeviceBundle.continuousServo(named(name))

        fun colorSensor(name: String) =
            this@DeviceBundle.colorSensor(named(name))

        fun encoder(name: String, cpr: Double = 360.0) =
            this@DeviceBundle.encoder(named(name), cpr)

        fun gyro(name: String) =
            this@DeviceBundle.gyro(named(name))

        fun touchSensor(name: String) =
            this@DeviceBundle.touchSensor(named(name))

        fun motorWithEncoder(
            name: String,
            direction: Motor.Direction = Motor.Direction.FORWARD,
            cpr: Double = 360.0,
            controller: (Double) -> Double = { it }
        ) = this@DeviceBundle.motorWithEncoder(named(name), direction, cpr, controller)

        operator fun String.invoke(block: NamedScope.() -> Unit) = withPrefix(this, block)

        fun withPrefix(first: String, block: NamedScope.() -> Unit) =
            NamedScope(named("") + first).apply(block)
    }

    fun withPrefix(prefix: String, block: NamedScope.() -> Unit) =
        NamedScope(prefix).apply(block)

    operator fun String.invoke(block: NamedScope.() -> Unit) = withPrefix(this, block)

    fun motor(name: String, direction: Motor.Direction = Motor.Direction.FORWARD) =
        Motor(name, direction).add()

    fun servo(name: String, range: ClosedFloatingPointRange<Double>) =
        Servo(name, range).add()

    fun continuousServo(name: String) =
        ContinuousServo(name).add()

    fun colorSensor(name: String) =
        ColorSensor(name).add()

    fun encoder(name: String, cpr: Double = 360.0) =
        Encoder(name, cpr).add()

    fun gyro(name: String) =
        Gyro(name).add()

    fun touchSensor(name: String) =
        TouchSensor(name).add()

    fun motorWithEncoder(
        name: String,
        direction: Motor.Direction = Motor.Direction.FORWARD,
        cpr: Double = 360.0,
        controller: (Double) -> Double
    ) {
        Motor(name, direction).add()
        Encoder(name, cpr).add()
        MotorWithEncoder(name, controller).add()
    }

    inline fun <reified T : Device> findByName(name: String) = lazy {
        devices.find {
            it is T && it.name == name
        } ?: throw RuntimeException("Can not find $name:${T::class.simpleName}")
    }

}
