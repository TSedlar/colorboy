package com.colorboy.internal.reflect

import java.util.*

/**
 * @author Tyler Sedlar
 * @since 4/9/2018
 */
object LoadedClasses {

    fun list(loader: ClassLoader): List<Class<*>> {
        val loaded: MutableList<Class<*>> = ArrayList()
        Classes.findField(ClassLoader::class.java, {
            return@findField it.toGenericString().contains("java.util.Vector<java.lang.Class<?>>")
        })?.let {
            @Suppress("UNCHECKED_CAST")
            loaded.addAll(it.get(loader) as Vector<Class<*>>)
        }
        return loaded
    }
}