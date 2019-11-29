package org.mechdancer.ftc.unitedbot.robot

import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.delegate
import org.mechdancer.ftclib.core.structure.monomeric.*
import org.mechdancer.ftclib.core.structure.monomeric.effector.ContinuousServo
import org.mechdancer.ftclib.core.structure.monomeric.effector.Servo
import java.util.AbstractSet

class Arm : AbstractStructure("arm", {
    continuousServo("cs") {
        enable = true
    }
    motorWithEncoder("matrix") {
        cpr = MotorWithEncoder.CPR.Matrix12V

    }
}) {
    private val servo: ContinuousServo by delegate()
    private val matrix: MotorWithEncoder by delegate()


}