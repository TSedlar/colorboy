package com.colorboy.macros

import com.colorboy.api.Macro
import com.colorboy.api.Schedule
import com.colorboy.api.fx.Game
import com.colorboy.api.fx.canny
import com.colorboy.api.fx.toImage
import java.awt.Graphics2D
import java.awt.image.BufferedImage

/**
 * @author Tyler Sedlar
 * @since 4/9/2018
 */
class TestMacro: Macro() {

    var edges: BufferedImage? = null

    @Schedule(10)
    fun collectData() {
        edges = Game.MAT.canny(60.0, 3.0).toImage()
    }

    override fun render(g: Graphics2D) {
        edges?.let {
            g.drawImage(edges, 0, 0, null)
        }
    }
}