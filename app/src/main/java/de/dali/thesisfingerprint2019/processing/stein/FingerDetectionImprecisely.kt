package de.dali.thesisfingerprint2019.processing.stein

import de.dali.thesisfingerprint2019.processing.Config.STEP_SIZE
import de.dali.thesisfingerprint2019.processing.Config.TRESHOLD_RED
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.utils.Utils
import org.opencv.core.Mat
import org.opencv.core.Rect
import javax.inject.Inject
import org.opencv.core.Core

class FingerDetectionImprecisely @Inject constructor() : ProcessingStep() {

    override val TAG: String
        get() = FingerDetectionImprecisely::class.java.simpleName

    override fun run(originalImage: Mat): Mat? {
        val matR = getRedChannelFromMat(originalImage)

        val xLeft = detectLeft(matR)
        val xRight = detectRight(matR)
        val yTop = detectTop(matR)
        val yBottom = detectBottom(matR)

        val roi = Rect(xLeft, yBottom, yTop - yBottom,  xRight - xLeft)

        val bmp = Utils.convertMatToBitMap(Mat(originalImage, roi))
        Utils.saveImages("abc", "abc", "abc", listOf(bmp!!),100)

        return Mat(originalImage, roi)
    }

    private fun getRedChannelFromMat(originalImage: Mat): Mat{
        val lRgb = ArrayList<Mat>(3)
        Core.split(originalImage, lRgb)

        return lRgb[0]
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
        var borderX = 0

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
        var borderY = 0

        originalImage?.run {
            for (y in cols() - 1 downTo 0  step STEP_SIZE) {
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