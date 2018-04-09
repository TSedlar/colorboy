package com.colorboy.internal.pref

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * @author Tyler Sedlar
 * @since 3/20/2018
 */
object RSPreferences {

    private val PROPS = Properties()

    var defaultWorld: Int
        get() = Integer.parseInt(PROPS[Keys.DEFAULT_WORLD].toString())
        set(world) {
            PROPS.setProperty(Keys.DEFAULT_WORLD, Integer.toString(world))
        }

    private interface Keys {
        companion object {
            const val DEFAULT_WORLD = "default_world"
        }
    }

    private fun loadProps(file: File, props: Properties) {
        if (file.exists()) {
            try {
                FileInputStream(file).use { props.load(it) }
            } catch (e: IOException) {
                System.err.println("Failed to load prefs.txt")
            }
        }
    }

    init {
        val mainProps = File(AppConfig.HOME, "prefs.txt")

        loadProps(mainProps, PROPS)

        // set default PROPS
        setIfInvalid(PROPS, Keys.DEFAULT_WORLD, "70")

        Runtime.getRuntime().addShutdownHook(Thread {
            try {
                FileOutputStream(mainProps).use { out -> PROPS.store(out, "App preferences") }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
    }

    private fun setIfInvalid(props: Properties, key: String, value: String) {
        if (props.getProperty(key) == null) {
            props.setProperty(key, value)
        }
    }
}
