package org.mechdancer.common.ftc


import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import org.firstinspires.ftc.teamcode.GyroCalibration

object Gyro : SensorEventListener {

    private val angle: DoubleArray = DoubleArray(3)
    private const val Ns = 1E-9
    private var timestamp = 0L
    private val offset: DoubleArray = DoubleArray(3)

    private var k = listOf(.0, .0, .0)

    val value
        get() = angle.mapIndexed { index, d -> d - offset[index] }
                .toDoubleArray()

    private var runTime = 0L

    val filtered
        get() = value.mapIndexed { index, d ->
            d - k[index] * (System.currentTimeMillis() - runTime)
        }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {

    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        val dT = (sensorEvent.timestamp - timestamp) * Ns
        sensorEvent.values.forEachIndexed { index, fl ->
            angle[index] += fl * dT
        }
        timestamp = sensorEvent.timestamp
    }

    fun register() {
        systemService<SensorManager>(Activity.SENSOR_SERVICE)
                .registerListener(this,
                        systemService<SensorManager>(Activity.SENSOR_SERVICE).getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                        SensorManager.SENSOR_DELAY_FASTEST)
        k = List(3) { GyroCalibration.file.readLines().getOrNull(it)?.toDoubleOrNull() ?: .0 }
        runTime = System.currentTimeMillis()
    }

    fun unregister() {
        systemService<SensorManager>(Activity.SENSOR_SERVICE)
                .unregisterListener(this)
        runTime = 0
    }

    fun reset() {
        unregister()
        register()
        angle.forEachIndexed { index, d ->
            offset[index] = d
        }
    }

}