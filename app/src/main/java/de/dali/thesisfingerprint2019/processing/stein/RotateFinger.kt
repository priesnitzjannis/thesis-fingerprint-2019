package de.dali.thesisfingerprint2019.processing.stein


import de.dali.thesisfingerprint2019.processing.Config.TRESHOLD_RED
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.utils.Utils
import de.dali.thesisfingerprint2019.utils.Utils.rotateImageByDegree
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.getRotationMatrix2D
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.sqrt

class RotateFinger @Inject constructor() : ProcessingStep() {
    var correctionAngle = 0.0

    override val TAG: String
        get() = RotateFinger::class.java.simpleName

    override fun run(originalImage: Mat): Mat? {
        val middle = calcCenterPointOfMat(originalImage)
        val pointPair = generatePointPair(middle, 20)

        val p1Contour = calcPointOnContour(pointPair.first, originalImage)
        val p2Contour = calcPointOnContour(pointPair.second, originalImage)

        val distanceP1P2 = euclideanDist(pointPair.first, pointPair.second)
        val distanceP1ToContour = euclideanDist(pointPair.first, p1Contour)
        val distanceP2ToContour = euclideanDist(pointPair.second, p2Contour)

        val angle = calcAngle(distanceP1P2, distanceP2ToContour, distanceP1ToContour)
        correctionAngle = 0.0 - angle

        val rotatedImage = rotateImageByDegree(correctionAngle, originalImage)

        val bmpOrg = Utils.convertMatToBitMap(rotatedImage)

        return rotatedImage
    }

    private fun calcAngle(distanceP1P2: Double, distanceP2ToContour: Double, distanceP1ToContour: Double): Double {
        return atan(distanceP1P2 / (distanceP2ToContour - distanceP1ToContour)) * 180 / PI
    }

    operator fun Point.minus(p: Point) = Point(this.x - p.x, this.y - p.y)

    private fun euclideanDist(first: Point, second: Point): Double {
        val diff = first - second
        return sqrt(diff.x * diff.x + diff.y * diff.y)
    }

    private fun calcPointOnContour(first: Point, originalImage: Mat): Point {
        var pointOnContour = Point()

        for (i in first.x.toInt() downTo 0) {
            val pixel = originalImage.get(first.y.toInt(), i)

            if (pixel[0] < TRESHOLD_RED) {
                pointOnContour = Point(first.y, i + 1.0)
                break
            }
        }

        return pointOnContour
    }

    private fun generatePointPair(middle: Point, i: Int): Pair<Point, Point> =
        Pair(Point(middle.x - i, middle.y), Point(middle.x + i, middle.y))

    private fun calcCenterPointOfMat(resultFrame: Mat): Point =
        Point(resultFrame.cols() / 2.0, resultFrame.rows() / 2.0)

}