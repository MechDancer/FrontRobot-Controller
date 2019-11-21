package org.mechdancer.flow

import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.mechdancer.common.extension.cast
import org.mechdancer.dependency.Component
import org.mechdancer.dependency.DynamicScope
import org.mechdancer.dependency.plusAssign
import org.mechdancer.flow.data.ColorSensorData
import org.mechdancer.flow.data.EncoderData
import org.mechdancer.flow.data.GamepadData
import org.mechdancer.flow.data.GyroData
import org.mechdancer.flow.struct.Device
import org.mechdancer.flow.struct.RobotComponent
import org.mechdancer.flow.struct.effector.ContinuousServo
import org.mechdancer.flow.struct.effector.Motor
import org.mechdancer.flow.struct.effector.Servo
import org.mechdancer.flow.struct.find
import org.mechdancer.flow.struct.sensor.*
import org.mechdancer.flow.struct.sensor.ColorSensor
import org.mechdancer.flow.struct.sensor.TouchSensor
import org.mechdancer.flow.struct.sensor.VoltageSensor
import org.mechdancer.flow.struct.sensor.gamepad.Gamepad
import java.io.Closeable

open class Robot(deviceConfig: DeviceBundle.() -> Unit = {}) : DynamicScope(), Closeable {

    val devices: MutableList<Device> = mutableListOf()

    private val availableDevices: MutableMap<Device, HardwareDevice> = mutableMapOf()


    var initialized = false
        private set

    //Sensors accessor
    private lateinit var encoders: Map<DcMotor, Encoder>
    private lateinit var gyros: Map<Gyroscope, Gyro>
    private lateinit var touches: Map<com.qualcomm.robotcore.hardware.TouchSensor, TouchSensor>
    private lateinit var colors: Map<com.qualcomm.robotcore.hardware.ColorSensor, ColorSensor>

    private var last = -1L

    init {
        setupDeviceBundle(deviceConfig)
    }

    fun <T : Component> T.attach() = apply { this@Robot.setup(this) }

    fun update(gamepad1: com.qualcomm.robotcore.hardware.Gamepad, gamepad2: com.qualcomm.robotcore.hardware.Gamepad) {
        period = System.currentTimeMillis() - last
        encoders.forEach { (hardware, device) ->
            device.update(EncoderData(hardware.currentPosition.toDouble(), hardware.cast<DcMotorEx>().velocity))
        }
        gyros.forEach { (hardware, device) ->
            device.update(
                hardware.getAngularVelocity(AngleUnit.RADIANS).let {
                    GyroData(
                        it.xRotationRate.toDouble(),
                        it.yRotationRate.toDouble(),
                        it.zRotationRate.toDouble()
                    )
                }
            )
        }
        touches.forEach { (hardware, device) ->
            device.update(hardware.isPressed)
        }
        colors.forEach { (hardware, device) ->
            device.update(
                ColorSensorData(
                    hardware.red(),
                    hardware.green(),
                    hardware.blue(),
                    hardware.alpha()
                )
            )
        }
        master.update(GamepadData.fromFTC(gamepad1))
        helper.update(GamepadData.fromFTC(gamepad2))
        last = System.currentTimeMillis()
    }


    /**
     * Master gamepad
     */
    val master = Gamepad(0)

    /**
     * Helper gamepad
     */
    val helper = Gamepad(1)

    /**
     * Voltage sensor of battery
     */
    val voltageSensor = VoltageSensor()

    /**
     * Robot loop period
     */
    @Volatile
    var period = 0L
        private set


    /**
     * Init use existing id
     */
    fun init(hardwareMap: HardwareMap) {

        //Avoid repeatedly call
        check(!initialized)

        val namedDevices = devices.associateBy { it.name }

        logger.info("Finding hardware devices")
        namedDevices.mapNotNull { (name, device) ->
            runCatching {
                logger.info("Attempting to find device $name")
                hardwareMap[name]
            }
                .onSuccess { logger.info("Found $name") }
                .onFailure { logger.warn("Failed to find $name") }
                .getOrNull()?.let { device to it }
        }.let { availableDevices.putAll(it) }

        //Setup devices
        setupAvailableDevices()

        //Call init
        components.mapNotNull { it as? RobotComponent }.forEach(RobotComponent::init)

        //Initialize sensor accessors
        initSensors()

        //Link outputs
        linkOutputs()

        logger.info("Initialized")
        initialized = true
    }

    private fun linkOutputs() {
        logger.info("Linking Motors")
        val devices = availableDevices.keys

        devices.find<Motor>().forEach { device ->
            device.power linkWithTransform {
                availableDevices[device].cast<DcMotor>().power = it
            }
        }

        logger.info("Linking Servos")
        devices.find<Servo>().forEach { device ->
            device.position linkWithTransform {
                availableDevices[device].cast<com.qualcomm.robotcore.hardware.Servo>().position = it
            }
            device.pwmEnable linkWithTransform {
                // TODO Need cast to controller
            }
        }
        logger.info("Linking CRServos")
        devices.find<ContinuousServo>().forEach { device ->
            device.power linkWithTransform {
                availableDevices[device].cast<CRServo>().power = it
            }
            device.pwmEnable linkWithTransform {
                // TODO Need cast to controller
            }
        }
    }

    private fun initSensors() {
        val devices = availableDevices.keys
        encoders = devices.find<Encoder>().associateBy { availableDevices[it].cast<DcMotor>() }
        gyros = devices.find<Gyro>().associateBy { availableDevices[it].cast<Gyroscope>() }
        touches = devices.find<TouchSensor>().associateBy { availableDevices[it].cast<com.qualcomm.robotcore.hardware.TouchSensor>() }
        colors = devices.find<ColorSensor>().associateBy { availableDevices[it].cast<com.qualcomm.robotcore.hardware.ColorSensor>() }
    }

    private fun setupAvailableDevices() {
        logger.info("Setup available devices: ${availableDevices.keys.joinToString { it.name }}")
        availableDevices.forEach { (it, _) ->
            this += it
        }
    }


    override fun close() {
        if (!initialized) return
        components.mapNotNull { it as? RobotComponent }.forEach(RobotComponent::stop)
        availableDevices.clear()
        logger.info("Breaking connections")
        breakAllConnections()
        initialized = false
    }

}
