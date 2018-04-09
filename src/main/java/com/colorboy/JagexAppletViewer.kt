package com.colorboy

import com.colorboy.api.util.Time
import com.colorboy.internal.io.Internet
import com.colorboy.internal.pref.RSPreferences
import com.colorboy.internal.reflect.Classes
import java.applet.Applet
import java.awt.*
import java.io.File
import java.io.IOException
import java.lang.invoke.MethodHandles
import java.lang.reflect.Field
import java.net.JarURLConnection
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile

/**
 * @author Tyler Sedlar
 * @since 3/20/2018
 */
object JagexAppletViewer {

    private const val VIEWER_URL = "http://oldschool.runescape.com/downloads/jagexappletviewer.jar"
    private const val CONFIG = "http://oldschool%s.runescape.com/l=en/jav_config.ws"

    @Throws(IOException::class)
    fun download(targetFile: String) {
        var outdated = true

        val jar = File(targetFile)

        if (jar.exists()) {
            val localHash = JarFile(jar).manifest.hashCode()
            val jarManifestURL = "jar:$VIEWER_URL!/META-INF/MANIFEST.MF"
            val remoteViewer = URL(jarManifestURL).openConnection() as JarURLConnection
            val remoteHash = remoteViewer.manifest.hashCode()
            if (localHash == remoteHash) { // The local and remote manifest hashcode match, already up-to-date
                outdated = false
            }
        }

        if (outdated) {
            // TODO: show download progress of jagexappletviewer.jar
            Internet.download(VIEWER_URL, jar.absolutePath)
        } else {
            // TODO: show that jagexappletviewer.jar already up-to-date
        }
    }

    @Throws(Throwable::class)
    fun run(targetFile: String): ColorBoy? {
        val jar = File(targetFile)
        val loader = URLClassLoader.newInstance(arrayOf(jar.toURI().toURL()))

        val main = loader.loadClass("jagexappletviewer")

        val exec = main.getDeclaredMethod("main", Array<String>::class.java)

        System.setProperty("com.jagex.config", String.format(CONFIG, RSPreferences.defaultWorld))
        System.setProperty("sun.java2d.nodraw", "true")

        val mainHandle = MethodHandles.lookup().unreflect(exec)
        val viewerArgs = arrayOf("./")
        mainHandle.invoke(*viewerArgs as Array<*>)

        val applet = findRSApplet(loader)
        val canvas = findRSCanvas(applet)

        hideAllButCanvas(canvas)

        return ColorBoy(applet, canvas)
    }

    @Suppress("DEPRECATION") // Applet is deprecated in Java9
    private fun findAppletField(loader: ClassLoader): Field {
        val viewer = loader.loadClass("app.appletviewer") ?: error("app.appletviewer not found")
        return Classes.findField(viewer) { field -> field.type == Applet::class.java } ?: error("Applet not found")
    }

    @Suppress("DEPRECATION") // Applet is deprecated in Java9
    private fun locateApplet(field: Field): Applet {
        for (i in 1..10) {
            val applet = field.get(null)
            if (applet != null) {
                return applet as Applet
            }
            Time.sleep(1000)
        }
        error("Unable to locate RSApplet")
    }

    @Suppress("DEPRECATION") // Applet is deprecated in Java9
    private fun findRSApplet(loader: ClassLoader): Applet {
        return locateApplet(findAppletField(loader))
    }

    @Suppress("DEPRECATION") // Applet is deprecated in Java9
    private fun findRSCanvas(applet: Applet): Canvas {
        for (i in 1..10) {
            if (applet.componentCount > 0) {
                return applet.getComponent(0) as Canvas
            }
            Time.sleep(1000)
        }
        error("Unable to locate RSCanvas")
    }

    private fun findWindow(canvas: Canvas?): Window {
        var component: Component? = canvas
        do {
            component = component?.parent ?: break
            if (component is Window) {
                return component
            }
        } while (component != null)
        error("Unable to find window")
    }

    @Suppress("DEPRECATION")
    private fun hideAllButCanvas(canvas: Canvas) {
        val window = findWindow(canvas)
        val components = window.components
        var panel: Panel? = null
        for (child in components) {
            if (child is Container) {
                panel = child as Panel
                for (gc in child.components) {
                    if (gc !is Panel) {
                        gc.isVisible = false
                        child.remove(gc)
                    }
                }
                break
            }
        }
        var i = 0
        while (i < 10 && canvas.mouseListeners.isEmpty()) { // allow the canvas to fully load
            Time.sleep(1000)
            i++
        }
        panel?.size = canvas.size
        window.minimumSize = Dimension(780, 542)
        window.revalidate()
        window.pack()
        window.setLocationRelativeTo(null)
        window.toFront()
        window.requestFocus()
        canvas.requestFocus()
    }
}
