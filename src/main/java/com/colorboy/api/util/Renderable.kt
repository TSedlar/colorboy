package com.colorboy.api.util

import java.awt.Graphics2D

/**
 * @author Tyler Sedlar
 * @since 4/9/2018
 */
interface Renderable {

    fun render(g: Graphics2D)
}