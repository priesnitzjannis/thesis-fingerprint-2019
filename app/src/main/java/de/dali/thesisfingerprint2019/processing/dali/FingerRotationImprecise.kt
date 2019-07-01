package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImage
import de.dali.thesisfingerprint2019.processing.Utils.releaseImage
import org.opencv.core.Mat
import org.opencv.core.Point
import javax.inject.Inject

class FingerRotationImprecise @Inject constructor() : ProcessingStep() {
    var correctionAngle = 0.0

    override val TAG: String
        get() = FingerRotationImprecise::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        val thresh = getThresholdImage(originalImage)

        val pointPair = generatePointPair(thresh, 50)

        releaseImage(listOf(thresh))

        val p1Contour = calcPointOnContour(pointPair.first, originalImage)
        val p2Contour = calcPointOnContour(pointPair.second, originalImage)

        val distanceP1P2 = Utils.euclideanDist(pointPair.first, pointPair.second)
        val distanceP1ToContour = Utils.euclideanDist(pointPair.first, p1Contour)
        val distanceP2ToContour = Utils.euclideanDist(pointPair.second, p2Contour)

        val angle = Utils.calcAngle(distanceP1P2, distanceP2ToContour, distanceP1ToContour)
        correctionAngle = -angle

        val rotatedImage = Utils.rotateImageByDegree(correctionAngle, originalImage)

        val bmpResult = Utils.convertMatToBitMap(rotatedImage)
        return rotatedImage
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    private fun generatePointPair(image: Mat, i: Int): Pair<Point, Point> {
        val colLow = if (image.cols() * 0.1 - i < 0) 0.0 else image.cols() * 0.1 - i
        val colHigh = image.cols() * 0.1 + i

        return Pair(Point(0.0, colLow), Point(0.0, colHigh))
    }

    private fun calcPointOnContour(point: Point, image: Mat): Point {
        var pointOnContour = Point()
        val imageThresh = getThresholdImage(image)

        for (i in point.x.toInt() until image.cols()) {
            val pixel = imageThresh.get(point.y.toInt(), i)

            if (pixel[0] != 0.0) {
                pointOnContour = Point(i.toDouble(), point.y)
                break
            }
        }

        return pointOnContour
    }

}