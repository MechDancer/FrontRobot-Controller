package org.mechdancer.ftc.unitedbot.robot

import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.delegate
import org.mechdancer.ftclib.core.structure.monomeric.MotorWithEncoder
import org.mechdancer.ftclib.core.structure.monomeric.continuousServo
import org.mechdancer.ftclib.core.structure.monomeric.effector.ContinuousServo
import org.mechdancer.ftclib.core.structure.monomeric.motorWithEncoder

class Arm : AbstractStructure("arm", {
    continuousServo("cs") {
        enable = true
    }
    motorWithEncoder("matrix") {
        cpr = -MotorWithEncoder.CPR.Matrix12V

    }
}) {
    val cs: ContinuousServo by delegate()
    val matrix: MotorWithEncoder by delegate()


}