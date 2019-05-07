package de.dali.thesisfingerprint2019.processing.stein

import de.dali.thesisfingerprint2019.processing.Config.TRESHOLD_RED
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import org.opencv.core.Mat
import org.opencv.core.Rect
import javax.inject.Inject


class FingerDetection @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = FingerDetection::class.java.simpleName

    override fun run(originalImage: Mat, processedImage: Mat): Mat? {
        val xLeft = detectLeft(processedImage)
        val xRight = detectRight(processedImage)
        val yTop = detectTop(processedImage)
        val yBottom = detectBottom(processedImage)

        val roi = Rect(xLeft, yBottom, xRight - xLeft, yTop - yBottom)
        return Mat(originalImage, roi)
    }

    private fun detectLeft(processedImage: Mat?): Int {
        var borderX = 0

        processedImage?.run {
            for (x in 0 until cols()) {
                for (y in 0 until rows()) {
                    if (get(x, y)[0] >= TRESHOLD_RED) {
                        borderX = x
                        return@run
                    }
                }
            }
        }
        return borderX
    }

    private fun detectRight(processedImage: Mat?): Int {
        var borderX = 0

        processedImage?.run {
            for (x in cols()-1 downTo 0) {
                for (y in 0 until rows()) {
                    if (get(x, y)[0] >= TRESHOLD_RED) {
                        borderX = x
                        return@run
                    }
                }
            }
        }
        return borderX
    }

    private fun detectTop(processedImage: Mat?): Int {
        var borderY = 0

        processedImage?.run {
            for (y in rows()-1 downTo 0) {
                for (x in 0 until cols()) {
                    if (get(x, y)[0] >= TRESHOLD_RED) {
                        borderY = y
                        return@run
                    }
                }
            }
        }
        return borderY
    }

    private fun detectBottom(processedImage: Mat?): Int {
        var borderY = 0

        processedImage?.run {
            for (y in 0 until rows()) {
                for (x in 0 until cols()) {
                    if (get(x, y)[0] >= TRESHOLD_RED) {
                        borderY = y
                        return@run
                    }
                }
            }
        }
        return borderY
    }
}