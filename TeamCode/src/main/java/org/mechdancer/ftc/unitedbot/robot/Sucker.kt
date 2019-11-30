package org.mechdancer.ftc.unitedbot.robot

import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.delegate
import org.mechdancer.ftclib.core.structure.monomeric.MotorWithEncoder
import org.mechdancer.ftclib.core.structure.monomeric.motorWithEncoder

class Sucker : AbstractStructure("sucker", {
    motorWithEncoder("motor") {
        cpr = MotorWithEncoder.CPR.Matrix12V
        enable = true
    }
}) {
    val motor: MotorWithEncoder by delegate()


}