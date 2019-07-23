package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.processing.Config.ROW_TO_COL_RATIO
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import org.opencv.core.Mat
import org.opencv.core.Rect
import javax.inject.Inject
import kotlin.math.ceil


class FindFingerTip @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = FindFingerTip::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        val cols = originalImage.cols()
        var newRows = ceil(cols * ROW_TO_COL_RATIO).toInt()

        newRows = if (newRows > originalImage.rows()) originalImage.rows() else newRows

        val rect = Rect(0, 0, cols, newRows)

        return Mat(originalImage, rect)
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }
}