package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix
import org.firstinspires.ftc.robotcore.external.navigation.*
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.mmPerInch
import org.mechdancer.ftclib.core.opmode.OpModeWithRobot
import org.mechdancer.ftclib.core.structure.MonomericStructure
import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.util.OpModeLifecycle
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.RADIANS
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC


class StoneFinder :
        MonomericStructure("stoneFinder"),
        OpModeLifecycle.Initialize<Robot>,
        OpModeLifecycle.Run {
    private val VUFORIA_KEY = "\n" +
            "AWcj0SH/////AAAAGc51QoZ0Lkf9qsnetlRxwNkVS4T/x5I75ZeQ5CmkxUGRVjxzxR/thQq0i7jaeZX1ZF9cnEsO+NSRhOye+9IQSqYq6TGgc8+iF75ERZAZBKABUd/a6H3ZwBIudWhNdrypQ4r41XjVdb8iBZaTDOqkjw+Zed1ZUkU3dv4HfN/c1nS3/AJLYrgd4zlopvUExBr6ynECCyjgmPXQCcTH6SZ6pph2ZiJXtIBTF/ZaalMXqd9gPMgj9dAy3Hb+Tz26AQFDQevRStLdWZHLdDwdM/a6da7/upBkcMA1dGgm9lQU1UOhneym92qVc3o/eBCKmJ20XehLT2rzbHNNrpHcAqWBSd1d5XW2b8PJIkNRTazIsGCs"

    internal lateinit var webcamName: WebcamName

    internal lateinit var vuforia:VuforiaLocalizer
    internal lateinit var stoneTarget: VuforiaTrackable

    private val phoneXRotate = 0f
    private val phoneYRotate = 0f
    private val phoneZRotate = 0f



    val CAMERA_FORWARD_DISPLACEMENT = 4.0f * mmPerInch   // eg: Camera is 4 Inches in front of robot-center
    val CAMERA_VERTICAL_DISPLACEMENT = 8.0f * mmPerInch   // eg: Camera is 8 Inches above ground
    val CAMERA_LEFT_DISPLACEMENT = 0f     // eg: Camera is ON the robot's center line


    val robotFromCamera = OpenGLMatrix
            .translation(CAMERA_FORWARD_DISPLACEMENT.toFloat(), CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT.toFloat())
            .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate))

    var StoneToRobot:OpenGLMatrix= OpenGLMatrix()
    fun getLocation()= arrayListOf(StoneToRobot.getTranslation().get(0),robotFromCamera.getTranslation().get(1),Orientation.getOrientation(robotFromCamera, EXTRINSIC, XYZ, RADIANS))



    override fun init(opMode: OpModeWithRobot<Robot>) {
        webcamName = opMode.hardwareMap.get(WebcamName::class.java, "Webcam 1")
        val cameraMonitorViewId = opMode.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", opMode.hardwareMap.appContext.getPackageName())
        val parameters = VuforiaLocalizer.Parameters(cameraMonitorViewId)
        parameters.vuforiaLicenseKey = VUFORIA_KEY
        parameters.cameraName = webcamName
        vuforia = ClassFactory.getInstance().createVuforia(parameters)
        val targetsSkyStone = this.vuforia.loadTrackablesFromAsset("Skystone")
        stoneTarget = targetsSkyStone[0]
        stoneTarget.name = "Stone Target"
        stoneTarget.location = OpenGLMatrix
                .translation(0f, 0f, 0f)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, 0f))
        (stoneTarget.getListener() as VuforiaTrackableDefaultListener).setPhoneInformation(robotFromCamera, parameters.cameraDirection)


    }

    override fun run() {
        StoneToRobot= (stoneTarget.getListener() as VuforiaTrackableDefaultListener).updatedRobotLocation.inverted()
    }

    override fun toString(): String {
        return ""
    }
}