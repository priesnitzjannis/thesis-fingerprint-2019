package de.dali.thesisfingerprint2019.processing.dali

import android.util.Log
import de.dali.thesisfingerprint2019.processing.Config.POINT_PAIR_DST
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.HAND
import de.dali.thesisfingerprint2019.processing.Utils.HAND.NOT_SPECIFIED
import de.dali.thesisfingerprint2019.processing.Utils.HAND.RIGHT
import de.dali.thesisfingerprint2019.processing.Utils.calcAngle
import de.dali.thesisfingerprint2019.processing.Utils.conditionalPointOnContour
import de.dali.thesisfingerprint2019.processing.Utils.euclideanDist
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImage
import de.dali.thesisfingerprint2019.processing.Utils.releaseImage
import de.dali.thesisfingerprint2019.processing.Utils.rotateImageByDegree
import org.opencv.core.Mat
import org.opencv.core.Point
import java.lang.Exception
import javax.inject.Inject


class FingerRotationImprecise @Inject constructor() : ProcessingStep() {
    var correctionAngle = 0.0

    var hand: HAND = NOT_SPECIFIED

    override val TAG: String
        get() = FingerRotationImprecise::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
//        val thresh = getThresholdImage(originalImage)
//
//        val pointPair = generatePointPair(thresh, POINT_PAIR_DST)
//
//        releaseImage(listOf(thresh))
//
//        val p1Contour = calcPointOnContour(pointPair.first, originalImage)
//        val p2Contour = calcPointOnContour(pointPair.second, originalImage)
//
//
//        val distanceP1P2 = euclideanDist(pointPair.first, pointPair.second)
//        val distanceP1ToContour = euclideanDist(pointPair.first, p1Contour)
//        val distanceP2ToContour = euclideanDist(pointPair.second, p2Contour)
//
//        val angle = calcAngle(distanceP1P2, distanceP2ToContour, distanceP1ToContour)
//        correctionAngle = if (hand == RIGHT) angle else -angle
//
//        return rotateImageByDegree(correctionAngle, originalImage)
        return originalImage
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    private fun generatePointPair(image: Mat, i: Int): Pair<Point, Point> {
        val rowLow = if (image.rows() * 0.9 + i > image.rows()) image.rows().toDouble() else image.rows() * 0.9 + i
        val rowHigh = image.rows() * 0.9 - i

        val x = if (hand == RIGHT) 0.0 else image.cols().toDouble()

        return Pair(Point(x, rowLow), Point(x, rowHigh))
    }

    private fun calcPointOnContour(point: Point, image: Mat): Point {
        var pointOnContour = Point()
        val imageThresh = getThresholdImage(image)

        try {
            conditionalPointOnContour(hand, point, image) { i ->
                val pixel = imageThresh.get(point.y.toInt() - 1, i - 1)

                if (pixel[0] != 0.0) {
                    pointOnContour = Point(i.toDouble(), point.y)
                    true
                } else {
                    false
                }
            }
        } catch (e : Exception){
            Log.e(TAG, e.message)
        }

        return pointOnContour
    }

}