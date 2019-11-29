package org.mechdancer.ftc.unitedbot.robot

import org.mechdancer.ftc.unitedbot.robot.UnitedBotArgs.LIFT_LEFT_POSITION_PID
import org.mechdancer.ftc.unitedbot.robot.UnitedBotArgs.LIFT_RIGHT_POSITION_PID
import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.delegate
import org.mechdancer.ftclib.core.structure.monomeric.MotorWithEncoder
import org.mechdancer.ftclib.core.structure.monomeric.motorWithEncoder

class Lift : AbstractStructure("lift",{
    motorWithEncoder("right"){
        cpr=MotorWithEncoder.CPR.Matrix12V
    }
    motorWithEncoder("lift"){
        cpr=MotorWithEncoder.CPR.Matrix12V
    }
}){
    val left: MotorWithEncoder by delegate()
    val right: MotorWithEncoder by delegate()


    var positionSign=false
    var targetPower=.0
    var rTargetPosition=.0
    var lTargetPosition=.0

    override fun run() {



        if (targetPower==.0){
            if (!positionSign){
                rTargetPosition=right.position
                lTargetPosition=left.position

            }
            else{
                LIFT_RIGHT_POSITION_PID.core(rTargetPosition-right.position)
                LIFT_LEFT_POSITION_PID.core(lTargetPosition-left.position)
            }


        }else{
            right.power=targetPower
            left.power=targetPower
        }


    }
}