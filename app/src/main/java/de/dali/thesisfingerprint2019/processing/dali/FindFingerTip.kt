package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.logging.Logging
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
        val start = System.currentTimeMillis()

        val cols = originalImage.cols()
        var newRows = ceil(cols * ROW_TO_COL_RATIO).toInt()

        newRows = if (newRows > originalImage.rows()) originalImage.rows() else newRows

        val rect = Rect(0, 0, cols, newRows)

        var result = Mat(originalImage, rect)


        val duration = System.currentTimeMillis() - start
        Logging.createLogEntry(Logging.loggingLevel_detailed, 1400, "Fingertip Location finished in " + duration + "ms.")

        Logging.createLogEntry(Logging.loggingLevel_critical, 1400, "Fingertip located, see image for results.", result)

        return result
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }
}