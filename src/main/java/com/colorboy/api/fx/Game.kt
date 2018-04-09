package com.colorboy.api.fx

import com.colorboy.ColorBoy
import com.colorboy.api.util.fx.toIntMat
import org.opencv.core.Mat
import java.awt.image.BufferedImage

/**
 * @author Tyler Sedlar
 * @since 4/9/2018
 */
object Game {

    val IMAGE: BufferedImage
        get() = ColorBoy.INSTANCE!!.modCanvas.raw!!

    val MAT: Mat
        get() = IMAGE.toIntMat()
}