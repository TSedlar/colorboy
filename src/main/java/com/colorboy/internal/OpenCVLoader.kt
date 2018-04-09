package com.colorboy.internal

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
 * @since 4/8/2018
 */
object OpenCVLoader {

    init {
        load()
    }

    fun is64System(): Boolean = System.getProperty("sun.arch.data.model").contains("64")

    fun load() {
        JavaLibraryPath.add("src/main/resources/lib/x${if (is64System()) "64" else "86"}/")
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
    }
}