import com.colorboy.api.fx.OpenCV
import com.colorboy.api.fx.canny
import com.colorboy.api.fx.findTemplates
import com.colorboy.api.fx.toImage
import com.colorboy.internal.OpenCVLoader
import org.opencv.core.Point
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO
import kotlin.system.measureTimeMillis

object TestOpenCV {

    val DEPOSIT_BOX = arrayOf(
            OpenCV.mat("./deposit-box/queries/north-query.png") to 0.25,
            OpenCV.mat("./deposit-box/queries/north-west-query.png") to 0.25
    )

    @JvmStatic
    fun main(args: Array<String>) {
        OpenCVLoader.load()
        Files.list(Paths.get("./deposit-box/game/")).forEach { path ->
            val game = OpenCV.mat(path.toFile().absolutePath).canny()
            val results: MutableList<Point> = ArrayList()

            measureTimeMillis {
                results.addAll(game.findTemplates(*DEPOSIT_BOX))
            }.let {
                println("${path.fileName}: matched ${results.size}x in ${it}ms")
            }

            val img = BufferedImage(game.width(), game.height(), BufferedImage.TYPE_INT_RGB)
            val gameImg = game.toImage()

            val g = img.graphics

            g.drawImage(gameImg, 0, 0, null)

            g.color = Color.GREEN

            results.forEach {
                g.drawRect(it.x.toInt(), it.y.toInt(), DEPOSIT_BOX[0].first.width(), DEPOSIT_BOX[0].first.height())
            }

            ImageIO.write(img, "png", File("./deposit-box/results/${path.fileName}"))
        }
    }
}