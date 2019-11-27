package org.firstinspires.ftc.teamcode

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
import org.mechdancer.algebra.implement.vector.vector2DOf
import org.mechdancer.common.ftc.Pose2D
import org.mechdancer.dataflow.core.broadcast
import org.mechdancer.dataflow.core.intefaces.ISource
import org.mechdancer.flow.struct.UniqueRobotComponent
import org.mechdancer.flow.struct.post
import org.mechdancer.flow.struct.sensor.Sensor
import org.mechdancer.geometry.angle.toRad
import java.util.concurrent.atomic.AtomicReference

class StoneFinder(private val useWebcam: Boolean = true) : UniqueRobotComponent<StoneFinder>(), Sensor<Pose2D> {

    companion object {

        private val VUFORIA_KEY =
            "ARUMLrz/////AAABmQJWUPMdxk0js1idkM5EVUAL7NbQ4tLNle+XIBvb776/3jHQz3ZPTqrLUvBD784620pjFp340UGGiVGQrPC+UOSFy3Bd2YXpiLXyD5/N0dRkAi99xPXGtNW9qiAA7gK5fWaDc4xHNEwPVFNLP21g3EQhX5ynDjMyE+togwh+IAT9W0Brc8G0t8eYeFKTMjyG0YhtXZVgtixcjuyC1/is0j4uW6MXzqM17nz2YkBD/mJSnGadaFXNTiMLoPv0EpeKt+dMzjN7+x7+ILeblBg1wVFQz5t1S+KLcEm7JUqemMKg5hx56xXxcdFnnWUteRaZ676UpsN048LZD49EHUb2AV0rE0w6PVpD+F9nJV7y57vA"

    }

    private lateinit var stoneTargetListener: VuforiaTrackableDefaultListener

    private lateinit var trackables: VuforiaTrackables

    private val robotFromCamera: OpenGLMatrix = OpenGLMatrix
        .translation(0f, 0f, 0f).let {
            if (useWebcam)
                it.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, 90f))
            else
                it.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, -90f))
        }

    private val pose = AtomicReference(Pose2D.zero())

    private var started = false

    val idealTargetOnRobot: Pose2D
        get() = pose.get()

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
        stoneTarget.location = OpenGLMatrix
            .translation(0f, 0f, 0f).multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90f, 0f, -90f))

        stoneTargetListener = (stoneTarget.listener as VuforiaTrackableDefaultListener).also {
            if (!useWebcam)
                it.setPhoneInformation(robotFromCamera, parameters.cameraDirection)
        }
        trackables.activate()

    }


    override val name: String = javaClass.simpleName

    override val updated: ISource<Pose2D> = broadcast()

    override fun update(new: Pose2D) {
        if (!started) return
        if (pose.getAndSet(new) != new)
            updated post new
    }

    fun update() {
        if (!started) return
        stoneTargetListener.robotLocation?.inverted()?.let {
            if (useWebcam)
                robotFromCamera.multiplied(it)
            else it
        }?.run {
            Pose2D(
                vector2DOf(
                    translation.get(0).toDouble(),
                    translation.get(1).toDouble()
                ),
                Orientation.getOrientation(this, EXTRINSIC, XYZ, RADIANS).thirdAngle.toRad()
            )
        }?.let {
            update(it)
        }
    }

    override fun stop() {
        if (!started) return
        if (this::trackables.isInitialized)
            trackables.deactivate()
        started = false
    }

}