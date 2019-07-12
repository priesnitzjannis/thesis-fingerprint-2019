package de.dali.thesisfingerprint2019.processing.dali

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

    override val TAG: String
        get() = MultiQualityAssurance::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        val point = calcCenterPoint(originalImage)
        val result = sobel(originalImage)

        edgeDensity = edgeDensity(result, point)

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

}