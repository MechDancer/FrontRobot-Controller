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
class StoneFinder(
    /**
     * 是否使用外置摄像头
     */
    private val useWebcam: Boolean = false
) {

    //------------------------------------------------------------------------------------------------
    // 状态
    //------------------------------------------------------------------------------------------------

    private lateinit var stoneTargetListener: VuforiaTrackableDefaultListener

    private lateinit var trackables: VuforiaTrackables


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
    var idealTargetOnRobot: Pose2D? = null
        private set

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
        trackables = vuforia.loadTrackablesFromAsset("Skystone")
        val stoneTarget = trackables[0]
        stoneTarget.name = "Stone Target"
        // 理想得分物坐标系，与三维机器人头超前的坐标系重合
        stoneTarget.location = OpenGLMatrix
            .translation(0f, 0f, 0f).multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, -90f))

        stoneTargetListener = (stoneTarget.listener as VuforiaTrackableDefaultListener).also {
            // 手机摄像头相对于外置摄像头会使 Z 轴翻转
            if (!useWebcam)
                it.setPhoneInformation(robotFromCamera, parameters.cameraDirection)
        }
        trackables.activate()
    }

    /**
     * 更新计算
     */
    fun update() {
        idealTargetOnRobot =
            stoneTargetListener.robotLocation?.inverted()?.let {
                // 外置摄像头需要手动乘 M ^ camera -> robot
                if (useWebcam)
                    robotFromCamera.multiplied(it)
                else it
            }?.run {
                val o = Orientation.getOrientation(this, EXTRINSIC, XYZ, RADIANS)
                Pose2D.pose(
                    translation.get(0).toDouble() * RATIO,
                    translation.get(1).toDouble() * RATIO,
                    o.thirdAngle.toDouble()
                )
            }
    }


    /**
     * 关闭识别器
     */
    fun stop() {
        if (this::trackables.isInitialized)
            trackables.deactivate()
    }


    companion object {
        private const val RATIO = .6 / 616
        private const val VUFORIA_KEY =
            "ARUMLrz/////AAABmQJWUPMdxk0js1idkM5EVUAL7NbQ4tLNle+XIBvb776/3jHQz3ZPTqrLUvBD784620pjFp340UGGiVGQrPC+UOSFy3Bd2YXpiLXyD5/N0dRkAi99xPXGtNW9qiAA7gK5fWaDc4xHNEwPVFNLP21g3EQhX5ynDjMyE+togwh+IAT9W0Brc8G0t8eYeFKTMjyG0YhtXZVgtixcjuyC1/is0j4uW6MXzqM17nz2YkBD/mJSnGadaFXNTiMLoPv0EpeKt+dMzjN7+x7+ILeblBg1wVFQz5t1S+KLcEm7JUqemMKg5hx56xXxcdFnnWUteRaZ676UpsN048LZD49EHUb2AV0rE0w6PVpD+F9nJV7y57vA"
    }

}