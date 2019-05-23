package de.dali.thesisfingerprint2019.processing.stein

import android.util.Log
import de.dali.thesisfingerprint2019.processing.Config.CENTER_OFFSET_X
import de.dali.thesisfingerprint2019.processing.Config.CENTER_OFFSET_Y
import de.dali.thesisfingerprint2019.processing.Config.CENTER_SIZE_X
import de.dali.thesisfingerprint2019.processing.Config.CENTER_SIZE_Y
import de.dali.thesisfingerprint2019.processing.Config.DDEPTH
import de.dali.thesisfingerprint2019.processing.Config.DELTA
import de.dali.thesisfingerprint2019.processing.Config.EDGE_DENS_TRESHOLD
import de.dali.thesisfingerprint2019.processing.Config.GRAD_X
import de.dali.thesisfingerprint2019.processing.Config.GRAD_Y
import de.dali.thesisfingerprint2019.processing.Config.K_SIZE_GAUS
import de.dali.thesisfingerprint2019.processing.Config.K_SIZE_SOBEL
import de.dali.thesisfingerprint2019.processing.Config.SCALE
import de.dali.thesisfingerprint2019.processing.Config.TRESHOLD_RED
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.utils.Utils.releaseImage
import org.opencv.core.*
import org.opencv.core.Core.*
import org.opencv.imgproc.Imgproc.*
import javax.inject.Inject

class QualityAssurance @Inject constructor() : ProcessingStep() {

    var edgeDensity: Double = 0.0

    override val TAG: String
        get() = QualityAssurance::class.java.simpleName

    override fun run(originalImage: Mat): Mat? {
        val point = calcCenterPoint(originalImage)
        val fingerInROI = fingerIsInROI(originalImage, point)

        if (fingerInROI) {
            val result = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_64FC1)
            val blurred = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_64FC1)
            val gray = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_64FC1)
            val grad_x = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_64FC1)
            val grad_y = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_64FC1)
            val abs_grad_x = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_64FC1)
            val abs_grad_y = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_64FC1)

            GaussianBlur(originalImage, blurred, Size(K_SIZE_GAUS, K_SIZE_GAUS), 0.0, 0.0, BORDER_DEFAULT)
            cvtColor(blurred, gray, COLOR_BGR2GRAY)
            Sobel(gray, grad_x, DDEPTH, 1, 0, K_SIZE_SOBEL, SCALE, DELTA, BORDER_DEFAULT)
            Sobel(gray, grad_y, DDEPTH, 0, 1, K_SIZE_SOBEL, SCALE, DELTA, BORDER_DEFAULT)

            convertScaleAbs(grad_x, abs_grad_x)
            convertScaleAbs(grad_y, abs_grad_y)

            addWeighted(abs_grad_x, GRAD_X, abs_grad_y, GRAD_Y, 0.0, result)

            releaseImage(
                listOf(
                    blurred,
                    gray,
                    grad_x,
                    grad_y,
                    abs_grad_x,
                    abs_grad_y
                )
            )

            edgeDensity = edgeDensity(result, point)

            Log.e(TAG, "edgeDens -> $edgeDensity")

            return if (edgeDensity > EDGE_DENS_TRESHOLD) {
                originalImage
            } else {
                null
            }
        } else {
            return null
        }
    }

    private fun calcCenterPoint(originalImage: Mat): Point {
        val pX = originalImage.cols() / 2 - CENTER_OFFSET_X
        val pY = originalImage.rows() / 2 - CENTER_OFFSET_Y

        return Point(pX, pY)
    }

    private fun fingerIsInROI(originalImage: Mat, pCenter: Point): Boolean {
        val startX = pCenter.x.toInt()
        val startY = pCenter.y.toInt()
        val endX = startX + CENTER_SIZE_X.toInt()
        val endY = startY + CENTER_SIZE_Y.toInt()

        val lRgb = ArrayList<Mat>(3)
        split(originalImage, lRgb)

        val imageR =  lRgb[0]

        var sum = 0.0

        for (i in startX until endX) {
            for (j in startY until endY) {
             sum += imageR.get(j, i)[0]
            }
        }

        val avg = sum/(CENTER_SIZE_X * CENTER_SIZE_Y)

        Log.e(TAG, "avgRed ---> $avg")

        return avg > TRESHOLD_RED
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