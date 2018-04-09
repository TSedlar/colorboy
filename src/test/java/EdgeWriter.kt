import com.colorboy.api.fx.canny
import com.colorboy.api.fx.toImage
import com.colorboy.internal.OpenCVLoader
import org.opencv.imgcodecs.Imgcodecs
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

object EdgeWriter {

    @JvmStatic
    fun main(args: Array<String>) {
        OpenCVLoader.load()

        Files.list(Paths.get("./deposit-box/game/")).forEach {
            val game = Imgcodecs.imread(it.toFile().absolutePath, Imgcodecs.IMREAD_COLOR)
            ImageIO.write(game.canny().toImage(), "png", File("./deposit-box/edges/${it.fileName}"))
        }
    }
}