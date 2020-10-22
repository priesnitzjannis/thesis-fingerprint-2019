package de.dali.demonstrator.processing.stein

import de.dali.demonstrator.processing.ProcessingStep
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect
import javax.inject.Inject

class FingerDetectionPrecisely @Inject constructor() : ProcessingStep() {

    override val TAG: String
        get() = FingerDetectionPrecisely::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        val center = Point(
            originalImage.cols() / 2.0,
            originalImage.rows() / 2.0
        )
        val xLeft = detectLeft(originalImage, center)
        val xRight = detectRight(originalImage, center)
        val yTop = detectTop(originalImage, center)
        val yBottom = detectBottom(originalImage, center)
        val roi = Rect(yBottom, xLeft, yTop - yBottom, xRight - xLeft)

        return Mat(originalImage, roi)
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    private fun detectLeft(originalImage: Mat?, center: Point): Int {
        var borderX = 0

        originalImage?.run {
            for (y in center.y.toInt() downTo 0) {
                if (get(y, center.x.toInt())[0] < 80) {
                    borderX = y
                    return@run
                }
            }
        }
        return borderX
    }

    private fun detectRight(originalImage: Mat?, center: Point): Int {
        var borderX = originalImage?.cols() ?: 0

        originalImage?.run {
            for (y in center.y.toInt() until rows()) {
                if (get(y, center.x.toInt())[0] < 80) {
                    borderX = y
                    return@run
                }
            }
        }
        return borderX
    }

    private fun detectTop(originalImage: Mat?, center: Point): Int {
        var borderY = originalImage?.cols() ?: 0

        originalImage?.run {
            for (y in center.x.toInt() until cols()) {
                if (get(center.y.toInt(), y)[0] < 80) {
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
            for (y in center.x.toInt() downTo 0) {
                if (get(center.y.toInt(), y)[0] < 80) {
                    borderY = y
                    return@run
                }
            }
        }
        return borderY
    }
}