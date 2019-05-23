package de.dali.thesisfingerprint2019.processing.stein

import de.dali.thesisfingerprint2019.processing.Config.MAX_KERNEL_LENGTH
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.utils.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc.*
import javax.inject.Inject

class Enhancement @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = Enhancement::class.java.simpleName

    override fun run(originalImage: Mat): Mat? {
        cvtColor(originalImage, originalImage, COLOR_BGR2GRAY)
        medianBlur(originalImage, originalImage, MAX_KERNEL_LENGTH)
        adaptiveThreshold(originalImage, originalImage, 255.0, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 9, 12.0)
        val bmp = Utils.convertMatToBitMap(originalImage)
        return originalImage
    }

}