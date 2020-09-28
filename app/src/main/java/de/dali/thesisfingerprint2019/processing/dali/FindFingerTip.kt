package de.dali.thesisfingerprint2019.processing.dali

import android.os.SystemClock.elapsedRealtimeNanos
import de.dali.thesisfingerprint2019.logging.Logging
import de.dali.thesisfingerprint2019.processing.Config
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
        Logging.createLogEntry(
            Logging.loggingLevel_param,
            1600,
            "Config data for Find Finger Tip:\nROW_TO_COL_RATIO = " + Config.ROW_TO_COL_RATIO
        )
        val start = elapsedRealtimeNanos()

        val cols = originalImage.cols()
        var newRows = ceil(cols * ROW_TO_COL_RATIO).toInt()

        newRows = if (newRows > originalImage.rows()) originalImage.rows() else newRows

        val rect = Rect(0, 0, cols, newRows)

        var result = Mat(originalImage, rect)


        val duration = elapsedRealtimeNanos() - start
        Logging.createLogEntry(Logging.loggingLevel_medium, 1600, "Fingertip Location finished in " + duration + "ns.")

        Logging.createLogEntry(Logging.loggingLevel_critical, 1600, "Fingertip located, see image for results.", result)

        return result
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }
}