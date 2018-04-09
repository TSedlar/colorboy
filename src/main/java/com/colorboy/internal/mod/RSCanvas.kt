package com.colorboy.internal.mod

import com.colorboy.ColorBoy
import com.colorboy.api.util.fx.GraphicsState
import java.awt.*
import java.awt.event.*
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.event.MouseInputAdapter

/**
 * @author Tyler Sedlar
 * @since 3/20/2018
 */
class RSCanvas(container: ColorBoy, var original: Canvas) : Canvas() {

    var buffer: BufferedImage? = null
    var raw: BufferedImage? = null

    private var normalized = false
    private var hidden = false

    private val initialMouseListeners: Array<MouseListener>
    private val initialMouseMotionListeners: Array<MouseMotionListener>
    private val initialMouseWheelListeners: Array<MouseWheelListener>
    private val initialKeyListeners: Array<KeyListener>
    var mouseInputAdapter: MouseInputAdapter? = null
    var keyInputAdapter: KeyAdapter? = null

    val consumers: MutableList<(Graphics2D) -> Unit> = ArrayList()

    init {
        bounds = original.bounds
        raw = BufferedImage(GAME_SIZE.width, GAME_SIZE.height, BufferedImage.TYPE_INT_RGB)
        buffer = BufferedImage(GAME_SIZE.width, GAME_SIZE.height, BufferedImage.TYPE_INT_RGB)
        initialMouseListeners = original.mouseListeners
        initialMouseMotionListeners = original.mouseMotionListeners
        initialMouseWheelListeners = original.mouseWheelListeners
        initialKeyListeners = original.keyListeners
        mouseInputAdapter = InputRedirector.createMouseAdapter(container, initialMouseListeners,
                initialMouseMotionListeners, initialMouseWheelListeners)
        keyInputAdapter = InputRedirector.createKeyAdapter(container, initialKeyListeners)
    }

    fun normalize() {
        normalized = true
    }

    fun hideGraphics() {
        hidden = true
    }

    override fun hasFocus(): Boolean {
        return true
    }

    override fun getGraphics(): Graphics? {
        val g = original.graphics
        if (hidden) {
            g!!.color = background
            g.fillRect(0, 0, width, height)
            return g
        }
        if (normalized) {
            return g
        }
        if (g != null && buffer != null && raw != null) {
            val paint = buffer!!.createGraphics()
            paint.drawImage(raw, 0, 0, null)
            consumers.forEach { consumer ->
                try {
                    val state = GraphicsState(paint)
                    consumer(paint)
                    state.restore()
                } catch (ignored: Exception) {
                    ignored.printStackTrace()
                }
            }
            paint.dispose()
            g.drawImage(buffer, 0, 0, null)
            g.dispose()
            return raw!!.createGraphics()
        }
        return null
    }

    override fun setLocation(x: Int, y: Int) {
        super.setLocation(x, y)
        original.setLocation(x, y)
    }

    override fun setLocation(p: Point?) {
        super.setLocation(p)
        original.location = p
    }

    private fun updateImage(w: Int, h: Int) {
        if (raw == null || raw!!.width != w || raw!!.height != h) {
            raw = BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
            buffer = BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
        }
    }

    override fun setSize(width: Int, height: Int) {
        super.setSize(width, height)
        original.setSize(width, height)
        updateImage(width, height)
    }

    override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        super.setBounds(x, y, width, height)
        original.setBounds(x, y, width, height)
        updateImage(width, height)
    }

    override fun hashCode(): Int {
        return original.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return original == other
    }

    companion object {

        val GAME_SIZE = Dimension(765, 503)
    }
}