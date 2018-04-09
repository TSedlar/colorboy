import com.colorboy.api.fx.*
import com.colorboy.internal.OpenCVLoader
import org.opencv.imgcodecs.Imgcodecs
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object WorldMapTest {

    @JvmStatic
    fun main(args: Array<String>) {
        OpenCVLoader.load()

        val map = OpenCV.mat("./src/test/world_map.png", Imgcodecs.IMREAD_GRAYSCALE).canny()
        val pos = OpenCV.mat("./src/test/current_location.png", Imgcodecs.IMREAD_GRAYSCALE).canny()

        val positions: MutableList<Rectangle> = ArrayList()

        map.findAllTemplates(pos to 0.125)?.let {
            positions.addAll(it)
        }

        println("matches: ${positions.size}")

        if (positions.isNotEmpty()) {
            println(positions[0])
        }

        val img = BufferedImage(map.width(), map.height(), BufferedImage.TYPE_INT_RGB)

        val g = img.graphics as Graphics2D

        g.drawImage(map.toImage(), 0, 0, null)

        g.color = Color.RED
        positions.forEach { g.fill(it) }

        ImageIO.write(img, "png", File("./src/test/region_match.png"))
    }
}