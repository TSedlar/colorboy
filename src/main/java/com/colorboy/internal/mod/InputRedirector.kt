package com.colorboy.internal.mod

import com.colorboy.ColorBoy
import java.awt.event.*
import javax.swing.event.MouseInputAdapter

/**
 * @author Tyler Sedlar
 * @since 3/21/2018
 */
object InputRedirector {

    @JvmStatic
    fun createMouseAdapter(container: ColorBoy, initialMouseListeners: Array<MouseListener>,
                           initialMouseMotionListeners: Array<MouseMotionListener>,
                           initialMouseWheelListeners: Array<MouseWheelListener>): MouseInputAdapter {
        return object : MouseInputAdapter() {
            override fun mouseEntered(e: MouseEvent) {
                initialMouseListeners.forEach { ml -> ml.mouseEntered(e) }
            }

            override fun mouseExited(e: MouseEvent) {
                initialMouseListeners.forEach { ml -> ml.mouseExited(e) }
            }

            override fun mouseClicked(e: MouseEvent) {
                initialMouseListeners.forEach { ml -> ml.mouseClicked(e) }
            }

            override fun mousePressed(e: MouseEvent) {
                initialMouseListeners.forEach { ml -> ml.mousePressed(e) }
            }

            override fun mouseReleased(e: MouseEvent) {
                initialMouseListeners.forEach { ml -> ml.mouseReleased(e) }
            }

            override fun mouseMoved(e: MouseEvent) {
                initialMouseMotionListeners.forEach { mml -> mml.mouseMoved(e) }
            }

            override fun mouseDragged(e: MouseEvent) {
                initialMouseMotionListeners.forEach { mml -> mml.mouseDragged(e) }
            }

            override fun mouseWheelMoved(e: MouseWheelEvent) {
                initialMouseWheelListeners.forEach { mml -> mml.mouseWheelMoved(e) }
            }
        }
    }

    @JvmStatic
    fun createKeyAdapter(container: ColorBoy, initialKeyListeners: Array<KeyListener>): KeyAdapter {
        return object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                initialKeyListeners.forEach { it.keyPressed(e) }
            }

            override fun keyReleased(e: KeyEvent) {
                initialKeyListeners.forEach { it.keyReleased(e) }
            }

            override fun keyTyped(e: KeyEvent) {
                initialKeyListeners.forEach { it.keyTyped(e) }
            }
        }
    }
}