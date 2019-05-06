package de.dali.thesisfingerprint2019.processing.stein

import de.dali.thesisfingerprint2019.processing.ProcessingStep
import org.opencv.core.Mat
import javax.inject.Inject

class FingerSegmentation @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = FingerSegmentation::class.java.simpleName

    override fun run(originalImage: Mat, processedImage: Mat): Mat? {
        return processedImage
    }

}