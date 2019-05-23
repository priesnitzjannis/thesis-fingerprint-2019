package de.dali.thesisfingerprint2019.processing.stein

import de.dali.thesisfingerprint2019.processing.Config
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect
import javax.inject.Inject

class FingerDetectionPrecisely @Inject constructor() : ProcessingStep() {

    override val TAG: String
        get() = FingerDetectionPrecisely::class.java.simpleName

    override fun run(originalImage: Mat): Mat? {
        val matR = getRedChannelFromMat(originalImage)

        val center = Point(
            originalImage.rows() / 2.0,
            originalImage.cols() / 2.0
        )

        val xLeft = detectLeft(matR, center)
        val xRight = detectRight(matR, center)
        val yTop = detectTop(matR, center)
        val yBottom = detectBottom(matR, center)

        val roi = Rect(xLeft, yBottom, xRight - xLeft, yTop - yBottom)
        return Mat(originalImage, roi)
    }

    private fun getRedChannelFromMat(originalImage: Mat): Mat {
        val lRgb = ArrayList<Mat>(3)
        Core.split(originalImage, lRgb)

        return lRgb[0]
    }

    private fun detectLeft(originalImage: Mat?, center: Point): Int {
        var borderX = 0

        originalImage?.run {
            for (x in center.x.toInt() downTo 0) {
                if (get(x, center.y.toInt())[0] < Config.TRESHOLD_RED) {
                    borderX = x
                    return@run
                }
            }
        }
        return borderX
    }

    private fun detectRight(originalImage: Mat?, center: Point): Int {
        var borderX = 0

        originalImage?.run {
            for (x in center.x.toInt() until rows() - 1) {
                if (get(x, center.y.toInt())[0] < Config.TRESHOLD_RED) {
                    borderX = x
                    return@run
                }
            }
        }
        return borderX
    }

    private fun detectTop(originalImage: Mat?, center: Point): Int {
        var borderY = 0

        originalImage?.run {
            for (y in center.y.toInt() until cols() - 1) {
                if (get(center.x.toInt(), y)[0] < Config.TRESHOLD_RED) {
                    borderY = y
                    return@run
                }
            }
        }
        return borderY
    }

    private fun detectBottom(originalImage: Mat?, center: Point): Int {
        var borderY = 0

        originalImage?.run {
            for (y in center.y.toInt() downTo 0) {
                if (get(center.x.toInt(), y)[0] < Config.TRESHOLD_RED) {
                    borderY = y
                    return@run
                }
            }
        }
        return borderY
    }
}