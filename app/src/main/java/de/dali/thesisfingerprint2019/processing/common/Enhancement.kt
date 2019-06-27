package de.dali.thesisfingerprint2019.processing.common

import de.dali.thesisfingerprint2019.processing.Config.MAX_KERNEL_LENGTH
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.convertMatToBitMap
import org.opencv.core.Core
import org.opencv.core.Core.subtract
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc.*
import javax.inject.Inject

class Enhancement @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = Enhancement::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        cvtColor(originalImage, originalImage, COLOR_BGR2GRAY)
        medianBlur(originalImage, originalImage, MAX_KERNEL_LENGTH)

        val g1 = Mat()
        val g2 = Mat()
        val dst = Mat()

        GaussianBlur(originalImage, g1, Size(3.0, 3.0), 0.0)
        GaussianBlur(originalImage, g2, Size(11.0, 11.0), 0.0)

        subtract(g1, g2, dst)

        equalizeHist(dst, dst)

        Core.bitwise_not( dst, dst )

        val bmp = convertMatToBitMap(dst)
        return dst
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

}