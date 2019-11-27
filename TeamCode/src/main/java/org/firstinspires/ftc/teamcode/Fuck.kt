package org.firstinspires.ftc.teamcode

import org.mechdancer.ftclib.core.opmode.BaseOpMode
import org.mechdancer.ftclib.core.structure.MonomericStructure
import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.core.structure.injector.Inject

class Fuck : Robot("fuck", false, A()) {
    @Inject
    lateinit var b: B
}

class A : AbstractStructure("a", B())
class B : MonomericStructure("b") {
    override fun run() {

    }

    override fun toString(): String = "b"
}

class FM : BaseOpMode<Fuck>() {
    override fun initTask() {
    }

    override fun loopTask() {
    }

    override fun stopTask() {
    }

}