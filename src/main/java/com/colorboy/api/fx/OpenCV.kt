package com.colorboy.api.fx

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.image.BufferedImage
import java.awt.image.PixelGrabber

/**
 * @author Tyler Sedlar
 * @since 4/9/2018
 */
object OpenCV {

    fun mat(imgPath: String): Mat {
        return Imgcodecs.imread(imgPath, Imgcodecs.IMREAD_UNCHANGED)
    }
}

fun Mat.canny(threshold: Double, ratio: Double): Mat {
    val edges = Mat()
    Imgproc.Canny(this, edges, threshold, threshold * ratio)
    return edges
}

fun Mat.canny(): Mat = canny(60.0, 3.0)

fun Mat.toImage(): BufferedImage {
    val data = ByteArray(width() * height() * elemSize().toInt())
    this.get(0, 0, data)
    val type = if (this.channels() == 1) {
        BufferedImage.TYPE_BYTE_GRAY
    } else {
        BufferedImage.TYPE_3BYTE_BGR
    }
    val out = BufferedImage(width(), height(), type)
    out.raster.setDataElements(0, 0, width(), height(), data)
    return out
}

fun Mat.pixels(): IntArray {
    PixelGrabber(toImage(), 0, 0, width(), height(), true).let {
        it.grabPixels()
        return it.pixels as IntArray
    }
}

fun Mat.findTemplates(vararg templates: Pair<Mat, Double>): List<Point> {
    val results: MutableList<Point> = ArrayList()

    templates.forEach {
        val template = it.first
        val threshold = it.second

        val result = Mat()

        Imgproc.matchTemplate(this, template, result, Imgproc.TM_CCOEFF_NORMED)
        Imgproc.threshold(result, result, 0.1, 1.0, Imgproc.THRESH_TOZERO)

        while (true) {
            val mml = Core.minMaxLoc(result)
            val pos = mml.maxLoc
            if (mml.maxVal >= threshold) {
                Imgproc.rectangle(this, pos, Point(pos.x + template.cols(), pos.y + template.rows()),
                        Scalar(0.0, 255.0, 0.0), 1)
                Imgproc.rectangle(result, pos, Point(pos.x + template.cols(), pos.y + template.rows()),
                        Scalar(0.0, 255.0, 0.0), -1)
                results.add(pos)
            } else {
                break
            }
        }
    }

    return results
}