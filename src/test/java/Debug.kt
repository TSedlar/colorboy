import com.colorboy.api.fx.OpenCV
import com.colorboy.api.fx.canny
import com.colorboy.internal.OpenCVLoader
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object Debug {

    @JvmStatic
    fun main(args: Array<String>) {
        OpenCVLoader.load()
        val img = OpenCV.mat("./test-image.png")
        val canny = img.canny()
        val contours: MutableList<MatOfPoint> = ArrayList()
        val hierarchy = Mat()
        Imgproc.findContours(canny, contours, hierarchy, 3, 2, Point(0.0, 0.0))

        val out = BufferedImage(canny.width(), canny.height(), BufferedImage.TYPE_INT_RGB)
        val g = out.graphics

//        g.drawImage(canny.toImage(), 0, 0, null)

        g.color = Color.GREEN

        contours.forEach {
            it.toArray().forEach {
                g.fillRect(it.x.toInt(), it.y.toInt(), 1, 1)
            }
        }

        ImageIO.write(out, "png", File("./debug.png"))
    }
}