package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils
import org.opencv.core.Mat
import org.opencv.core.Rect
import javax.inject.Inject
import kotlin.math.ceil


class FindFingerTip @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = FindFingerTip::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        val cols = originalImage.cols()
        val newRows = ceil(cols * 1.5).toInt()
        val rect = Rect(0, 0, cols, newRows)

        val result = Mat(originalImage, rect)
        val bmp = Utils.convertMatToBitMap(result)

        return result
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }
}