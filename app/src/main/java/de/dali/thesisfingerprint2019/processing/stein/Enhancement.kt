package de.dali.thesisfingerprint2019.processing.stein

import de.dali.thesisfingerprint2019.processing.Config.MAX_KERNEL_LENGTH
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc.*
import javax.inject.Inject

class Enhancement @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = Enhancement::class.java.simpleName

    override fun run(originalImage: Mat, processedImage: Mat): Mat? {
        cvtColor(processedImage, processedImage, COLOR_BGR2GRAY)
        medianBlur(processedImage, processedImage, MAX_KERNEL_LENGTH)
        adaptiveThreshold(processedImage, processedImage, 125.0, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 19, 12.0)

        return processedImage
    }

}