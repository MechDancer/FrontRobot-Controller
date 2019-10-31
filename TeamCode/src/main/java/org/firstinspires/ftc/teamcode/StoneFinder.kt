package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.RADIANS
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.mmPerInch
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables
import org.mechdancer.ftclib.core.opmode.OpModeWithRobot
import org.mechdancer.ftclib.core.structure.MonomericStructure
import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.util.AutoCallable
import org.mechdancer.ftclib.util.OpModeLifecycle


class StoneFinder :
    MonomericStructure("stoneFinder"),
    AutoCallable,
    OpModeLifecycle.Initialize<Robot>,
    OpModeLifecycle.Stop {


    private lateinit var stoneTargetListener: VuforiaTrackableDefaultListener

    private lateinit var trackables: VuforiaTrackables

    companion object {
        private const val CAMERA_FORWARD_DISPLACEMENT = 4.0f * mmPerInch    // eg: Camera is 4 Inches in front of robot-center
        private const val CAMERA_VERTICAL_DISPLACEMENT = 8.0f * mmPerInch   // eg: Camera is 8 Inches above ground
        private const val CAMERA_LEFT_DISPLACEMENT = 0f                             // eg: Camera is ON the robot's center line

        private const val PHONE_X_ROTATE = 0f
        private const val PHONE_Y_ROTATE = 0f
        private const val PHONE_Z_ROTATE = 0f

        private val VUFORIA_KEY =
            "AWcj0SH/////AAAAGc51QoZ0Lkf9qsnetlRxwNkVS4T/x5I75ZeQ5CmkxUGRVjxzxR/thQq0i7jaeZX1ZF9cnEsO+NSRhOye+9IQSqYq6TGgc8+iF75ERZAZBKABUd/a6H3ZwBIudWhNdrypQ4r41XjVdb8iBZaTDOqkjw+Zed1ZUkU3dv4HfN/c1nS3/AJLYrgd4zlopvUExBr6ynECCyjgmPXQCcTH6SZ6pph2ZiJXtIBTF/ZaalMXqd9gPMgj9dAy3Hb+Tz26AQFDQevRStLdWZHLdDwdM/a6da7/upBkcMA1dGgm9lQU1UOhneym92qVc3o/eBCKmJ20XehLT2rzbHNNrpHcAqWBSd1d5XW2b8PJIkNRTazIsGCs"

    }

    private val robotFromCamera: OpenGLMatrix = OpenGLMatrix
        .translation(CAMERA_FORWARD_DISPLACEMENT.toFloat(), CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT.toFloat())
        .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, PHONE_Y_ROTATE, PHONE_Z_ROTATE, PHONE_X_ROTATE))


    var location: ArrayList<Double>? = null
        private set

    override fun init(opMode: OpModeWithRobot<Robot>) {
        with(opMode) {
            val webcamName = hardwareMap["Webcam 1"] as WebcamName
            val cameraMonitorViewId = hardwareMap.appContext.resources.getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.packageName)
            val parameters = VuforiaLocalizer.Parameters(cameraMonitorViewId)
            parameters.vuforiaLicenseKey = VUFORIA_KEY
            parameters.cameraName = webcamName
            val vuforia = ClassFactory.getInstance().createVuforia(parameters)
            trackables = vuforia.loadTrackablesFromAsset("Skystone")
            val stoneTarget = trackables[0]
            stoneTarget.name = "Stone Target"
            stoneTarget.location = OpenGLMatrix
                .translation(0f, 0f, 0f)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, 0f))
            stoneTargetListener = (stoneTarget.listener as VuforiaTrackableDefaultListener).also { it.setPhoneInformation(robotFromCamera, parameters.cameraDirection) }
            trackables.activate()
        }
    }

    override fun run() {
        location =
            stoneTargetListener.robotLocation?.inverted()?.run {
                arrayListOf(
                    translation.get(0).toDouble(),
                    translation.get(1).toDouble(),
                    Orientation.getOrientation(this, EXTRINSIC, XYZ, RADIANS).thirdAngle.toDouble()
                )
            }
    }


    override fun stop() {
        trackables.deactivate()
    }

    override fun toString(): String = javaClass.simpleName

}