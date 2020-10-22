package de.dali.demonstrator.processing.stein

import android.util.Log
import de.dali.demonstrator.processing.Config.CENTER_OFFSET_X
import de.dali.demonstrator.processing.Config.CENTER_OFFSET_Y
import de.dali.demonstrator.processing.Config.CENTER_SIZE_X
import de.dali.demonstrator.processing.Config.CENTER_SIZE_Y
import de.dali.demonstrator.processing.Config.EDGE_DENS_TRESHOLD
import de.dali.demonstrator.processing.Config.TRESHOLD_RED
import de.dali.demonstrator.processing.ProcessingStep
import de.dali.demonstrator.processing.Utils.sobel
import org.opencv.core.*
import org.opencv.core.Core.mean
import org.opencv.core.Core.split
import org.opencv.imgproc.Imgproc.fillPoly
import javax.inject.Inject

class QualityAssurance @Inject constructor() : ProcessingStep() {

    var edgeDensity: Double = 0.0

    override val TAG: String
        get() = QualityAssurance::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        val point = calcCenterPoint(originalImage)
        val fingerInROI = fingerIsInROI(originalImage, point)

        return if (fingerInROI) {
            val result = sobel(originalImage)
            edgeDensity = edgeDensity(result, point)

            Log.e(TAG, "edgeDens -> $edgeDensity")

            if (edgeDensity > EDGE_DENS_TRESHOLD) {
                originalImage
            } else {
                Mat()
            }
        } else {
            Mat()
        }
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    private fun calcCenterPoint(originalImage: Mat): Point {
        val pX = originalImage.cols() / 2 - CENTER_OFFSET_X
        val pY = originalImage.rows() / 2 - CENTER_OFFSET_Y

        return Point(pX, pY)
    }

    private fun fingerIsInROI(originalImage: Mat, pCenter: Point): Boolean {

        val points = mutableListOf(
            Point(pCenter.x, pCenter.y),
            Point(pCenter.x, pCenter.y + CENTER_SIZE_Y),
            Point(pCenter.x + CENTER_SIZE_X, pCenter.y),
            Point(pCenter.x + CENTER_SIZE_X, pCenter.y + CENTER_SIZE_Y)
        )

        val mask = Mat(
            Size(
                originalImage.cols().toDouble(),
                originalImage.rows().toDouble()
            ),
            CvType.CV_8UC1
        )

        val matPt = MatOfPoint()
        matPt.fromList(points)

        val ppt = ArrayList<MatOfPoint>()
        ppt.add(matPt)

        fillPoly(mask, ppt, Scalar(255.0), 4, 1)

        val rgb = ArrayList<Mat>(3)
        split(originalImage, rgb)

        val imageR = rgb[0]
        val average = mean(imageR, mask)

        Log.e(TAG, "avgRed ---> ${average.`val`[0].toInt()}/${average.`val`[1].toInt()}/${average.`val`[2].toInt()}")

        return average.`val`[0] >= TRESHOLD_RED
    }

    private fun edgeDensity(processedImage: Mat, pCenter: Point): Double {
        var sum = 0.0

        val startX = pCenter.x.toInt()
        val startY = pCenter.y.toInt()
        val endX = startX + CENTER_SIZE_X.toInt()
        val endY = startY + CENTER_SIZE_Y.toInt()

        for (i in startX until endX) {
            for (j in startY until endY) {
                sum += processedImage.get(j, i)[0]
            }
        }

        return sum / (CENTER_SIZE_X * CENTER_SIZE_Y)
    }

}