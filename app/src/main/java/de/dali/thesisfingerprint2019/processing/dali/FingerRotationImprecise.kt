package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.HAND
import de.dali.thesisfingerprint2019.processing.Utils.HAND.NOT_SPECIFIED
import de.dali.thesisfingerprint2019.processing.Utils.calcAngle
import de.dali.thesisfingerprint2019.processing.Utils.conditionalPointOnContour
import de.dali.thesisfingerprint2019.processing.Utils.convertMatToBitMap
import de.dali.thesisfingerprint2019.processing.Utils.euclideanDist
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImage
import de.dali.thesisfingerprint2019.processing.Utils.releaseImage
import de.dali.thesisfingerprint2019.processing.Utils.rotateImageByDegree
import org.opencv.core.Mat
import org.opencv.core.Point
import javax.inject.Inject

class FingerRotationImprecise @Inject constructor() : ProcessingStep() {
    var correctionAngle = 0.0

    var hand: HAND = NOT_SPECIFIED

    override val TAG: String
        get() = FingerRotationImprecise::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        val thresh = getThresholdImage(originalImage)

        val pointPair = generatePointPair(thresh, 50)

        releaseImage(listOf(thresh))

        val p1Contour = calcPointOnContour(pointPair.first, originalImage)
        val p2Contour = calcPointOnContour(pointPair.second, originalImage)

        val distanceP1P2 = euclideanDist(pointPair.first, pointPair.second)
        val distanceP1ToContour = euclideanDist(pointPair.first, p1Contour)
        val distanceP2ToContour = euclideanDist(pointPair.second, p2Contour)

        val angle = calcAngle(distanceP1P2, distanceP2ToContour, distanceP1ToContour)
        correctionAngle = -angle

        val rotatedImage = rotateImageByDegree(correctionAngle, originalImage)

        val bmpResult = convertMatToBitMap(rotatedImage)
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

        conditionalPointOnContour(hand, point, image) { i ->
            val pixel = imageThresh.get(point.y.toInt(), i)

            if (pixel[0] != 0.0) {
                pointOnContour = Point(i.toDouble(), point.y)
                true
            } else {
                false
            }
        }

        return pointOnContour
    }

}