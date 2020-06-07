package de.dali.thesisfingerprint2019.processing.common


import de.dali.thesisfingerprint2019.logging.Logging
import de.dali.thesisfingerprint2019.processing.Config.POINT_PAIR_DST
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils
import de.dali.thesisfingerprint2019.processing.Utils.HAND.NOT_SPECIFIED
import de.dali.thesisfingerprint2019.processing.Utils.HAND.RIGHT
import de.dali.thesisfingerprint2019.processing.Utils.calcAngle
import de.dali.thesisfingerprint2019.processing.Utils.conditionalPointOnContour
import de.dali.thesisfingerprint2019.processing.Utils.euclideanDist
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImage
import de.dali.thesisfingerprint2019.processing.Utils.rotateImageByDegree
import org.opencv.core.Mat
import org.opencv.core.Point
import javax.inject.Inject
import kotlin.math.roundToInt

class RotateFinger @Inject constructor() : ProcessingStep() {

    var degreeImprecise = 0.0

    var correctionAngle = 0.0

    var hand = NOT_SPECIFIED

    override val TAG: String
        get() = RotateFinger::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        val start = System.currentTimeMillis()

        val middle = calcCenterPointOfMat(originalImage)
        val pointPair = generatePointPair(middle, POINT_PAIR_DST)

        val p1Contour = calcPointOnContour(pointPair.first, originalImage)
        val p2Contour = calcPointOnContour(pointPair.second, originalImage)

        val distanceP1P2 = euclideanDist(pointPair.first, pointPair.second)
        val distanceP1ToContour = euclideanDist(pointPair.first, p1Contour)
        val distanceP2ToContour = euclideanDist(pointPair.second, p2Contour)

        val angle = calcAngle(distanceP1P2, distanceP2ToContour, distanceP1ToContour)
        val angleFixed = if (hand == RIGHT) 90 - angle else -(90 + angle)

        correctionAngle = if (angleFixed + degreeImprecise < -100.0 && hand == Utils.HAND.LEFT) angleFixed + 180.0
        else if (angleFixed + degreeImprecise > 100.0 && hand == RIGHT) angleFixed - 180.0
        else angleFixed

        val rotatedFinger = rotateImageByDegree(correctionAngle, originalImage)


        val duration = System.currentTimeMillis() - start
        Logging.createLogEntry(Logging.loggingLevel_detailed, 1300, "Finger Rotation finished in " + duration + "ms.")

        // could add rotation degree to the image, represented by line
        Logging.createLogEntry(Logging.loggingLevel_detailed, 1300, "Finger rotated by " + correctionAngle.roundToInt() + "°, see image for results.", rotatedFinger)

        return rotatedFinger
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    private fun calcPointOnContour(point: Point, image: Mat): Point {
        var pointOnContour = Point()
        val imageThresh = getThresholdImage(image)

        conditionalPointOnContour(NOT_SPECIFIED, point, image) { i ->
            val pixel = imageThresh.get(point.y.toInt(), i)

            if (pixel[0] == 0.0) {
                pointOnContour = Point(i.toDouble(), point.y)
                true
            } else {
                false
            }
        }

        return pointOnContour
    }

    private fun generatePointPair(middle: Point, i: Int): Pair<Point, Point> =
        Pair(Point(middle.x, middle.y - i), Point(middle.x, middle.y + i))

    private fun calcCenterPointOfMat(resultFrame: Mat): Point =
        Point(resultFrame.cols() / 2.0, resultFrame.rows() / 2.0)

}