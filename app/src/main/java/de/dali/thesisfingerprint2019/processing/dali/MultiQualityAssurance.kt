package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.logging.Logging
import de.dali.thesisfingerprint2019.processing.Config.CENTER_OFFSET_X
import de.dali.thesisfingerprint2019.processing.Config.CENTER_OFFSET_Y
import de.dali.thesisfingerprint2019.processing.Config.CENTER_SIZE_X
import de.dali.thesisfingerprint2019.processing.Config.CENTER_SIZE_Y
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.sobel
import org.opencv.core.Mat
import org.opencv.core.Point
import javax.inject.Inject

class MultiQualityAssurance @Inject constructor() : ProcessingStep() {

    var edgeDensity: Double = 0.0
    var validImageSize: Boolean = false

    override val TAG: String
        get() = MultiQualityAssurance::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        val point = calcCenterPoint(originalImage)
        val result = sobel(originalImage)
        Logging.createLogEntry(Logging.loggingLevel_medium, 1900, "Sobel result", result)

        edgeDensity = edgeDensity(result, point)
        validImageSize = hasValidSize(originalImage)
        return originalImage
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    private fun calcCenterPoint(originalImage: Mat): Point {
        val pY = originalImage.cols() / 2 - CENTER_OFFSET_X
        val pX = originalImage.rows() / 2 - CENTER_OFFSET_Y

        return Point(pX, pY)
    }

    private fun edgeDensity(processedImage: Mat, pCenter: Point): Double {
        var sum = 0.0

        val startX = pCenter.x.toInt()
        val startY = pCenter.y.toInt()
        val endX =
            if (startX + CENTER_SIZE_X.toInt() > processedImage.rows()) processedImage.rows() else startX + CENTER_SIZE_X.toInt()
        val endY =
            if (startY + CENTER_SIZE_Y.toInt() > processedImage.cols()) processedImage.cols() else startY + CENTER_SIZE_Y.toInt()

        for (i in startX until endX) {
            for (j in startY until endY) {
                sum += processedImage.get(i, j)[0]
            }
        }

        return sum / (CENTER_SIZE_X * CENTER_SIZE_Y)
    }

    // Here would be a good starting point for visual feedback 
    private fun hasValidSize(mat: Mat): Boolean {
        validImageSize = mat.cols() < 333 && mat.cols() > 150 && mat.rows() < 500 && mat.rows() > 225 && mat.rows().toDouble() / mat.cols().toDouble() < 1.75 && (mat.rows().toDouble() / mat.cols().toDouble()) > 1.25

        return validImageSize
    }

}