package org.mechdancer.flow.struct

import org.mechdancer.dependency.*

abstract class UniqueRobotComponent<T : UniqueRobotComponent<T>>
    : UniqueComponent<T>(), RobotComponent, Dependent {
    protected val manager = DependencyManager()
    override fun sync(dependency: Component): Boolean = manager.sync(dependency)

    override fun toString(): String = javaClass.simpleName.toLowerCase()

    fun String.joinPrefix() = "${this@UniqueRobotComponent}.$this"

    inline fun <reified T : NamedComponent<T>> DependencyManager.find(name: String) =
        must<T>(name.joinPrefix())
}