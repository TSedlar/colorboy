package com.colorboy.macros

import com.colorboy.api.Macro
import com.colorboy.api.Schedule
import com.colorboy.api.fx.*
import org.opencv.core.Mat
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import kotlin.system.measureTimeMillis

/**
 * @author Tyler Sedlar
 * @since 4/9/2018
 */
class TestMacro: Macro() {

    val DEPOSIT_BOX = OpenCV.modelRange("./src/main/resources/models/deposit-box", 1..14, 0.225)

    var canny: Mat? = null
    var edges: BufferedImage? = null
    var match: Rectangle? = null

    @Schedule(10)
    fun collectData() {
//        canny = Game.MAT.canny()
//        edges = canny!!.toImage()
    }

    @Schedule(10)
    fun findData() {
//        measureTimeMillis {
//            match = canny?.findFirstTemplate(*DEPOSIT_BOX.toTypedArray())
//        }.let {
//            println("${it}ms")
//        }
    }

    override fun render(g: Graphics2D) {
        edges?.let {
            g.drawImage(edges, 0, 0, null)
            g.color = Color.GREEN
            match?.let { g.draw(it) }
        }
    }
}