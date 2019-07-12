package de.dali.thesisfingerprint2019.processing.common


import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.calcAngle
import de.dali.thesisfingerprint2019.processing.Utils.convertMatToBitMap
import de.dali.thesisfingerprint2019.processing.Utils.euclideanDist
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImage
import de.dali.thesisfingerprint2019.processing.Utils.rotateImageByDegree
import org.opencv.core.Mat
import org.opencv.core.Point
import javax.inject.Inject

class RotateFinger @Inject constructor() : ProcessingStep() {
    var correctionAngle = 0.0

    override val TAG: String
        get() = RotateFinger::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        val middle = calcCenterPointOfMat(originalImage)
        val pointPair = generatePointPair(middle, 50)

        val p1Contour = calcPointOnContour(pointPair.first, originalImage)
        val p2Contour = calcPointOnContour(pointPair.second, originalImage)

        val distanceP1P2 = euclideanDist(pointPair.first, pointPair.second)
        val distanceP1ToContour = euclideanDist(pointPair.first, p1Contour)
        val distanceP2ToContour = euclideanDist(pointPair.second, p2Contour)

        val angle = calcAngle(distanceP1P2, distanceP2ToContour, distanceP1ToContour)
        correctionAngle = -(90 - angle)

        val rotatedImage = rotateImageByDegree(correctionAngle, originalImage)

        val bmpOrg = convertMatToBitMap(rotatedImage)

        return rotatedImage
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    private fun calcPointOnContour(point: Point, image: Mat): Point {
        var pointOnContour = Point()
        val imageThresh = getThresholdImage(image)

        for (i in point.x.toInt() until image.cols()) {
            val pixel = imageThresh.get(point.y.toInt(), i)

            if (pixel[0] == 0.0) {
                pointOnContour = Point(i.toDouble(), point.y)
                break
            }
        }

        return pointOnContour
    }


    private fun generatePointPair(middle: Point, i: Int): Pair<Point, Point> =
        Pair(Point(middle.x, middle.y - i), Point(middle.x, middle.y + i))

    private fun calcCenterPointOfMat(resultFrame: Mat): Point =
        Point(resultFrame.cols() / 2.0, resultFrame.rows() / 2.0)

}