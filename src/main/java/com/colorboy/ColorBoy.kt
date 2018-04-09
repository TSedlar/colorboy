package com.colorboy

import com.colorboy.api.Macro
import com.colorboy.api.Schedule
import com.colorboy.api.util.Time
import com.colorboy.internal.OpenCVLoader
import com.colorboy.internal.mod.ModSystem
import com.colorboy.internal.mod.RSCanvas
import com.colorboy.internal.pref.AppConfig
import com.colorboy.macros.TestMacro
import java.applet.Applet
import java.awt.Canvas
import java.io.File

/**
 * @author Tyler Sedlar
 * @since 4/9/2018
 */
@Suppress("DEPRECATION") // Applet is deprecated in J9
class ColorBoy(val applet: Applet, val canvas: Canvas) {

    val loader: ClassLoader
    val modCanvas: RSCanvas
    var currentMacro: Macro? = null

    init {
        INSTANCE = this

        loader = applet.javaClass.classLoader

        modCanvas = ModSystem.replaceCanvas(this)
        modCanvas.consumers.add({
            currentMacro?.render(it)
        })

        useMacro(TestMacro())
    }

    fun useMacro(macro: Macro) {
        currentMacro = macro
        // print out macro data
        macro.javaClass.methods.forEach { method ->
            val schedule = method.getAnnotation(Schedule::class.java)
            if (schedule != null) {
                Thread({
                    var errored = false
                    while (!errored && currentMacro == macro) {
                        try {
                            method.invoke(macro)
                            Time.sleep(schedule.interval.toLong())
                        } catch (e: Exception) {
                            e.printStackTrace()
                            errored = true
                        }
                    }
                }).start()
            }
        }
    }

    companion object {

        var INSTANCE: ColorBoy? = null

        @JvmStatic
        fun main(args: Array<String>) {
            OpenCVLoader.load()
            val viewerJar = AppConfig.path(AppConfig.JAGEX, "jagexappletviewer.jar")
            try {
                JagexAppletViewer.download(viewerJar)
            } catch (e: Exception) {
                println("Failed to download new jagexappletviewer.jar..")
            }

            if (File(viewerJar).exists()) {
                JagexAppletViewer.run(viewerJar)
            }
        }
    }
}