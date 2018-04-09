package com.colorboy.api.util.fx

import org.opencv.core.CvType
import org.opencv.core.Mat
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.DataBufferInt

/**
 * @author Tyler Sedlar
 * @since 4/9/2018
 */
object Imaging {
}

fun BufferedImage.toIntMat(): Mat {
    val mat = Mat(this.height, this.width, CvType.CV_8UC3)
    val dataBuff = (this.raster.dataBuffer as DataBufferInt).data

    val data = ByteArray(this.width * this.height * mat.elemSize().toInt())

    for (i in 0 until dataBuff.size) {
        data[i * 3] = (dataBuff[i] shr 16 and 0xFF).toByte()
        data[i * 3 + 1] = (dataBuff[i] shr 8 and 0xFF).toByte()
        data[i * 3 + 2] = (dataBuff[i] shr 0 and 0xFF).toByte()
    }

    mat.put(0, 0, data)
    return mat
}

fun BufferedImage.toByteMat(): Mat {
    val mat = Mat(this.height, this.width, CvType.CV_8UC3)
    val data = (this.raster.dataBuffer as DataBufferByte).data
    mat.put(0, 0, data)
    return mat
}