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
        processedImage?.run {
            for (x in 0..rows()) {
                for (y in 0..cols()) {
                    if (get(x, y)[0] >= TRESHOLD_RED) {
                        return@run x
                    }
                }
            }
        }
        return 0
    }

    private fun detectRight(processedImage: Mat?): Int {
        processedImage?.run {
            for (x in rows() downTo 0) {
                for (y in cols() downTo 0) {
                    if (get(x, y)[0] >= TRESHOLD_RED) {
                        return@run x
                    }
                }
            }
        }
        return 0
    }

    private fun detectTop(processedImage: Mat?): Int {
        processedImage?.run {
            for (y in cols() downTo 0) {
                for (x in 0..cols()) {
                    if (get(x, y)[0] >= TRESHOLD_RED) {
                        return@run x
                    }
                }
            }
        }
        return 0
    }

    private fun detectBottom(processedImage: Mat?): Int {
        processedImage?.run {
            for (y in 0..cols()) {
                for (x in 0..rows()) {
                    if (get(x, y)[0] >= TRESHOLD_RED) {
                        return@run x
                    }
                }
            }
        }
        return 0
    }
}