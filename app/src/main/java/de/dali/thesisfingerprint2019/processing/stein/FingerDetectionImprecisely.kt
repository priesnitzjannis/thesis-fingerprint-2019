package de.dali.thesisfingerprint2019.processing.stein

import de.dali.thesisfingerprint2019.processing.Config.STEP_SIZE
import de.dali.thesisfingerprint2019.processing.Config.TRESHOLD_RED
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.convertMatToBitMap
import org.opencv.core.Mat
import org.opencv.core.Rect
import javax.inject.Inject

class FingerDetectionImprecisely @Inject constructor() : ProcessingStep() {

    override val TAG: String
        get() = FingerDetectionImprecisely::class.java.simpleName

    override fun run(originalImage: Mat): Mat {

        val xLeft = detectLeft(originalImage)
        val xRight = detectRight(originalImage)
        val yTop = detectTop(originalImage)
        val yBottom = detectBottom(originalImage)
        val roi = Rect(yBottom, xLeft, yTop - yBottom, xRight - xLeft)

        return Mat(originalImage, roi)
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    private fun detectLeft(originalImage: Mat?): Int {
        var borderX = 0

        originalImage?.run {
            for (x in 0 until rows() step STEP_SIZE) {
                for (y in 0 until cols() step STEP_SIZE) {
                    if (get(x, y)[0] >= TRESHOLD_RED) {
                        borderX = x
                        return@run
                    }
                }
            }
        }
        return borderX
    }

    private fun detectRight(originalImage: Mat?): Int {
        var borderX = originalImage?.rows() ?: 0

        originalImage?.run {
            for (x in rows() - 1 downTo 0 step STEP_SIZE) {
                for (y in 0 until cols() step STEP_SIZE) {
                    if (get(x, y)[0] >= TRESHOLD_RED) {
                        borderX = x
                        return@run
                    }
                }
            }
        }
        return borderX
    }

    private fun detectTop(originalImage: Mat?): Int {
        var borderY = originalImage?.cols() ?: 0

        originalImage?.run {
            for (y in cols() - 1 downTo 0 step STEP_SIZE) {
                for (x in 0 until rows() step STEP_SIZE) {
                    if (get(x, y)[0] >= TRESHOLD_RED) {
                        borderY = y
                        return@run
                    }
                }
            }
        }
        return borderY
    }

    private fun detectBottom(originalImage: Mat?): Int {
        var borderY = 0

        originalImage?.run {
            for (y in 0 until cols() step STEP_SIZE) {
                for (x in 0 until rows() step STEP_SIZE) {
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