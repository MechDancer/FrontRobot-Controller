package org.mechdancer.flow.data

import com.qualcomm.robotcore.hardware.Gamepad


data class EncoderData(val position: Double, val speed: Double)

data class GamepadData(
    val leftBumper: Boolean,
    val rightBumper: Boolean,
    val aButton: Boolean,
    val bButton: Boolean,
    val xButton: Boolean,
    val yButton: Boolean,
    val upButton: Boolean,
    val downButton: Boolean,
    val leftButton: Boolean,
    val rightButton: Boolean,
    val leftStickX: Double,
    val leftStickY: Double,
    val leftStickButton: Boolean,
    val rightStickX: Double,
    val rightStickY: Double,
    val rightStickButton: Boolean,
    val leftTrigger: Double,
    val rightTrigger: Double
) {
    companion object {
        fun fromFTC(gamepad: Gamepad) =
            with(gamepad) {
                GamepadData(
                    left_bumper,
                    right_bumper,
                    a,
                    b,
                    x,
                    y,
                    dpad_up,
                    dpad_down,
                    dpad_left,
                    dpad_right,
                    left_stick_x.toDouble(),
                    left_stick_y.toDouble(),
                    left_stick_button,
                    right_stick_x.toDouble(),
                    right_stick_y.toDouble(),
                    right_stick_button,
                    left_trigger.toDouble(),
                    right_trigger.toDouble()
                )
            }

    }
}

data class ColorSensorData(
    val r: Int,
    val g: Int,
    val b: Int,
    val a: Int
)

data class GyroData(
    val pitchRate: Double,
    val yawRate: Double,
    val rollRate: Double
)