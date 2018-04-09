package com.colorboy.internal.mod

import com.colorboy.ColorBoy
import com.colorboy.api.util.Time
import com.colorboy.internal.reflect.Classes
import com.colorboy.internal.reflect.LoadedClasses
import java.applet.Applet
import java.awt.Canvas
import java.awt.Component
import java.awt.Image
import java.lang.reflect.Field

/**
 * @author Tyler Sedlar
 * @since 4/8/2018
 */
object ModSystem {

    @Suppress("DEPRECATION") // Applet is deprecated in J9
    fun replaceCanvas(container: ColorBoy): RSCanvas {
        val canvas = RSCanvas(container, container.canvas)
        val classes = LoadedClasses.list(container.loader)
        val producerClass = findProducerClass(classes) ?: error("404: ComponentProducer")
        val producerComponent = findProducerComponent(producerClass) ?: error("404: ComponentProducer#component")
        val producerInstance = findProducerInstance(classes, producerClass) ?: error("404: ComponentProducer#instance")
        Thread {
            while (true) {
                try {
                    canvas.original = container.applet.getComponent(0) as Canvas
//                    canvas.bounds = canvas.original.bounds
                    canvas.original.mouseListeners.forEach { canvas.original.removeMouseListener(it) }
                    canvas.original.mouseMotionListeners.forEach { canvas.original.removeMouseMotionListener(it) }
                    canvas.original.mouseWheelListeners.forEach { canvas.original.removeMouseWheelListener(it) }
                    canvas.original.keyListeners.forEach { canvas.original.removeKeyListener(it) }
                    canvas.original.addMouseListener(canvas.mouseInputAdapter)
                    canvas.original.addMouseMotionListener(canvas.mouseInputAdapter)
                    canvas.original.addMouseWheelListener(canvas.mouseInputAdapter)
                    canvas.original.addKeyListener(canvas.keyInputAdapter)
                    findCanvasField(container.applet)?.set(container.applet, canvas)
                    producerComponent.set(producerInstance.get(null), canvas)
                    Time.sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
        return canvas
    }
}

@Suppress("DEPRECATION") // Applet is deprecated in J9
private fun findCanvasField(applet: Applet): Field? {
    return Classes.findField(applet.javaClass.superclass, {
        return@findField it.type == Canvas::class.java
    })
}

private fun findProducerClass(classes: List<Class<*>>): Class<*>? {
    return classes.find {
        return@find Classes.findField(it, { it.type == Component::class.java }) != null &&
                Classes.findField(it, { it.type == Image::class.java }) != null
    }
}

private fun findProducerComponent(producer: Class<*>): Field? {
    return Classes.findField(producer, { it.type == Component::class.java })
}

private fun findProducerInstance(classes: List<Class<*>>, producer: Class<*>): Field? {
    classes.forEach {
        Classes.findField(it, { it.type == producer || it.type == producer.superclass })?.let {
            return it
        }
    }
    return null
}