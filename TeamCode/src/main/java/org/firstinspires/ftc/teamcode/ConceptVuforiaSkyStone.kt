package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener


@TeleOp(name = "SKYSTONE Vuforia", group = "Concept")
@Disabled
class ConceptVuforiaSkyStone : LinearOpMode() {

    // Class Members
    private var lastLocation: OpenGLMatrix = OpenGLMatrix()
    private lateinit var vuforia: VuforiaLocalizer
    private var targetVisible = false

    override fun runOpMode() {
        val cameraMonitorViewId = hardwareMap.appContext.resources.getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.packageName)
        val parameters = VuforiaLocalizer.Parameters(cameraMonitorViewId)


        parameters.vuforiaLicenseKey = VUFORIA_KEY
        parameters.cameraDirection = CAMERA_CHOICE

        vuforia = ClassFactory.getInstance().createVuforia(parameters)

        val targetsSkyStone = vuforia.loadTrackablesFromAsset("Skystone")

        val stoneTarget = targetsSkyStone[0]
        stoneTarget.name = "Stone Target"

        /**
         * In order for localization to work, we need to tell the system where each target is on the field, and
         * where the phone resides on the robot.  These specifications are in the form of *transformation matrices.*
         * Transformation matrices are a central, important concept in the math here involved in localization.
         * See [Transformation Matrix](https://en.wikipedia.org/wiki/Transformation_matrix)
         * for detailed information. Commonly, you'll encounter transformation matrices as instances
         * of the [OpenGLMatrix] class.
         *
         * If you are standing in the Red Alliance Station looking towards the center of the field,
         * - The X axis runs from your left to the right. (positive from the center to the right)
         * - The Y axis runs from the Red Alliance Station towards the other side of the field
         * where the Blue Alliance Station is. (Positive is from the center, towards the BlueAlliance station)
         * - The Z axis runs from the floor, upwards towards the ceiling.  (Positive is above the floor)
         *
         * Before being transformed, each target image is conceptually located at the origin of the field's
         * coordinate system (the center of the field), facing up.
         */


        // WARNING:
        // In this sample, we do not wait for PLAY to be pressed.  Target Tracking is started immediately when INIT is pressed.
        // This sequence is used to enable the new remote DS Camera Preview feature to be used with this sample.
        // CONSEQUENTLY do not put any driving commands in this loop.
        // To restore the normal opmode structure, just un-comment the following line:

        // waitForStart();

        // Note: To use the remote camera preview:
        // AFTER you hit Init on the Driver Station, use the "options menu" to select "Camera Stream"
        // Tap the preview window to receive a fresh image.

        targetsSkyStone.activate()
        while (!isStopRequested) {

            // check all the trackable targets to see which one (if any) is visible.
            targetVisible = false
            for (trackable in targetsSkyStone) {
                if ((trackable.listener as VuforiaTrackableDefaultListener).isVisible) {
                    telemetry.addData("Visible Target", trackable.name)
                    targetVisible = true

                    val robotLocationTransform = (trackable.listener as VuforiaTrackableDefaultListener).ftcCameraFromTarget
                    if (robotLocationTransform != null) {
                        lastLocation = robotLocationTransform
                    }
                    break
                }
            }

            // Provide feedback as to where the robot is located (if we know).
            if (targetVisible) {
                // express position (translation) of robot in inches.
                val translation = lastLocation.translation
                telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                    translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch)

                // express the rotation of the robot in degrees.
                val rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES)
                telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle)
            } else {
                telemetry.addData("Visible Target", "none")
            }
            telemetry.update()
        }

        // Disable Tracking when we are done;
        targetsSkyStone.deactivate()
    }

    companion object {

        // IMPORTANT:  For Phone Camera, set 1) the camera source and 2) the orientation, based on how your phone is mounted:
        // 1) Camera Source.  Valid choices are:  BACK (behind screen) or FRONT (selfie side)
        // 2) Phone Orientation. Choices are: PHONE_IS_PORTRAIT = true (portrait) or PHONE_IS_PORTRAIT = false (landscape)
        //
        // NOTE: If you are running on a CONTROL HUB, with only one USB WebCam, you must select CAMERA_CHOICE = BACK; and PHONE_IS_PORTRAIT = false;
        //
        private val CAMERA_CHOICE = BACK
        private val PHONE_IS_PORTRAIT = false

        private val VUFORIA_KEY = " -- YOUR NEW VUFORIA KEY GOES HERE  --- "

        // Since ImageTarget trackables use mm to specifiy their dimensions, we must use mm for all the physical dimension.
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
    }
}
