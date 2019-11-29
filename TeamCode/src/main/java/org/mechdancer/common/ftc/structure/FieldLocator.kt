package org.mechdancer.common.ftc.structure

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.RADIANS
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables
import org.mechdancer.common.ftc.Pose2D

/**
 * 石头定位器
 *
 * 用于在自动阶段计算带图石头在机器人坐标系位姿
 *
 * ** 摄像头画面只能存在一个带图得分物 **
 */
class FieldLocator(
    /**
     * 是否使用外置摄像头
     */
    private val useWebcam: Boolean = false
) {

    //------------------------------------------------------------------------------------------------
    // 状态
    //------------------------------------------------------------------------------------------------


    private lateinit var targetsSkyStone: VuforiaTrackables


    // 外置摄像头与手机摄像头定义坐标系不同
    private val robotFromCamera: OpenGLMatrix = OpenGLMatrix
        .translation(0f, 0f, 0f).let {
            if (useWebcam)
                it.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, 90f))
            else
                it.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, -90f))
        }

    /**
     * 得分物在机器人坐标系位姿
     */
    var RobotOnField: Pose2D? = null
        private set


    // We will define some constants and conversions here
    private val mmPerInch = 25.4f
    private val mmTargetHeight = 6 * mmPerInch          // the height of the center of the target image above the floor

    // Constant for Stone Target
    private val stoneZ = 2.00f * mmPerInch

    // Constants for the center support targets
    private val bridgeZ = 6.42f * mmPerInch
    private val bridgeY = 23 * mmPerInch
    private val bridgeX = 5.18f * mmPerInch
    private val bridgeRotY = 59f                                 // Units are degrees
    private val bridgeRotZ = 180f

    // Constants for perimeter targets
    private val halfField = 72 * mmPerInch
    private val quadField = 36 * mmPerInch

    private var lastLocation:OpenGLMatrix?=null

    /**
     * 初始化识别器并开启摄像头（耗时）
     */
    fun init(hardwareMap: HardwareMap) {



        val cameraMonitorViewId = hardwareMap.appContext.resources.getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.packageName)
        val parameters = VuforiaLocalizer.Parameters(cameraMonitorViewId)
        parameters.vuforiaLicenseKey = VUFORIA_KEY
        if (useWebcam) {
            val webcamName = hardwareMap["Webcam 1"] as WebcamName
            parameters.cameraName = webcamName
        } else
            parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK
        val vuforia = ClassFactory.getInstance().createVuforia(parameters)
        targetsSkyStone = vuforia.loadTrackablesFromAsset("Skystone")




        // set position of tags--------------------------------
        val blueRearBridge = targetsSkyStone[1]
        blueRearBridge.name = "Blue Rear Bridge"
        val redRearBridge = targetsSkyStone[2]
        redRearBridge.name = "Red Rear Bridge"
        val redFrontBridge = targetsSkyStone[3]
        redFrontBridge.name = "Red Front Bridge"
        val blueFrontBridge = targetsSkyStone[4]
        blueFrontBridge.name = "Blue Front Bridge"
        val red1 = targetsSkyStone[5]
        red1.name = "Red Perimeter 1"
        val red2 = targetsSkyStone[6]
        red2.name = "Red Perimeter 2"
        val front1 = targetsSkyStone[7]
        front1.name = "Front Perimeter 1"
        val front2 = targetsSkyStone[8]
        front2.name = "Front Perimeter 2"
        val blue1 = targetsSkyStone[9]
        blue1.name = "Blue Perimeter 1"
        val blue2 = targetsSkyStone[10]
        blue2.name = "Blue Perimeter 2"
        val rear1 = targetsSkyStone[11]
        rear1.name = "Rear Perimeter 1"
        val rear2 = targetsSkyStone[12]
        rear2.name = "Rear Perimeter 2"

        //Set the position of the bridge support targets with relation to origin (center of field)
        blueFrontBridge.location = OpenGLMatrix
            .translation(-bridgeX, bridgeY, bridgeZ)
            .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0f, bridgeRotY, bridgeRotZ))

        blueRearBridge.location = OpenGLMatrix
            .translation(-bridgeX, bridgeY, bridgeZ)
            .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0f, -bridgeRotY, bridgeRotZ))

        redFrontBridge.location = OpenGLMatrix
            .translation(-bridgeX, -bridgeY, bridgeZ)
            .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0f, -bridgeRotY, 0f))

        redRearBridge.location = OpenGLMatrix
            .translation(bridgeX, -bridgeY, bridgeZ)
            .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0f, bridgeRotY, 0f))

        //Set the position of the perimeter targets with relation to origin (center of field)
        red1.location = OpenGLMatrix
            .translation(quadField, -halfField, mmTargetHeight)
            .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, 180f))

        red2.location = OpenGLMatrix
            .translation(-quadField, -halfField, mmTargetHeight)
            .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, 180f))

        front1.location = OpenGLMatrix
            .translation(-halfField, -quadField, mmTargetHeight)
            .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, 90f))

        front2.location = OpenGLMatrix
            .translation(-halfField, quadField, mmTargetHeight)
            .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, 90f))

        blue1.location = OpenGLMatrix
            .translation(-quadField, halfField, mmTargetHeight)
            .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, 0f))

        blue2.location = OpenGLMatrix
            .translation(quadField, halfField, mmTargetHeight)
            .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, 0f))

        rear1.location = OpenGLMatrix
            .translation(halfField, quadField, mmTargetHeight)
            .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, -90f))

        rear2.location = OpenGLMatrix
            .translation(halfField, -quadField, mmTargetHeight)
            .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, -90f))


        //set end-------------------------------

        //drop the stoneTarget
        targetsSkyStone.drop(1)

        for (trackable in targetsSkyStone) {
            (trackable.listener as VuforiaTrackableDefaultListener).setPhoneInformation(robotFromCamera, parameters.cameraDirection)
        }




        targetsSkyStone.activate()
    }

    /**
     * 更新计算
     */
    fun update() {


        var targetVisible = false

        for (trackable in targetsSkyStone) {
            if ((trackable.getListener() as VuforiaTrackableDefaultListener).isVisible) {
                targetVisible = true

                // getUpdatedRobotLocation() will return null if no new information is available since
                // the last time that call was made, or if the trackable is not currently visible.
                val robotLocationTransform = (trackable.getListener() as VuforiaTrackableDefaultListener).updatedRobotLocation
                lastLocation = robotLocationTransform

                break
            }
        }

        // Provide feedback as to where the robot is located (if we know).
        if (targetVisible) run {

            RobotOnField=lastLocation?.let { if (useWebcam)
            it.multiplied(robotFromCamera.inverted())
            else it }?.run {
                val o = Orientation.getOrientation(this, EXTRINSIC, XYZ, RADIANS)
                Pose2D.pose(
                    translation.get(0).toDouble() * RATIO,
                    translation.get(1).toDouble() * RATIO,
                    o.thirdAngle.toDouble()
                )
            }

            // express the rotation of the robot in degrees.
        }

    }


    /**
     * 关闭识别器
     */
    fun stop() {
        if (this::targetsSkyStone.isInitialized)
            targetsSkyStone.deactivate()
    }


    companion object {
        private const val RATIO = .6 / 616
        private const val VUFORIA_KEY =
            "ARUMLrz/////AAABmQJWUPMdxk0js1idkM5EVUAL7NbQ4tLNle+XIBvb776/3jHQz3ZPTqrLUvBD784620pjFp340UGGiVGQrPC+UOSFy3Bd2YXpiLXyD5/N0dRkAi99xPXGtNW9qiAA7gK5fWaDc4xHNEwPVFNLP21g3EQhX5ynDjMyE+togwh+IAT9W0Brc8G0t8eYeFKTMjyG0YhtXZVgtixcjuyC1/is0j4uW6MXzqM17nz2YkBD/mJSnGadaFXNTiMLoPv0EpeKt+dMzjN7+x7+ILeblBg1wVFQz5t1S+KLcEm7JUqemMKg5hx56xXxcdFnnWUteRaZ676UpsN048LZD49EHUb2AV0rE0w6PVpD+F9nJV7y57vA"
    }

}