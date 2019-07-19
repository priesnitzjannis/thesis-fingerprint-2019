package de.dali.thesisfingerprint2019.processing.common

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
        val listOfKernelSizes = listOf(64.0, 32.0, 16.0, 8.0, 4.0, 2.0)

        for (i in 0..5) {
            val clahe = createCLAHE(2.0, Size(listOfKernelSizes[i], listOfKernelSizes[i]))
            clahe.apply(originalImage, originalImage)
        }

        val g1 = Mat()
        val g2 = Mat()
        val dst = Mat()

        GaussianBlur(originalImage, g1, Size(3.0, 3.0), 0.0)
        GaussianBlur(originalImage, g2, Size(7.0, 7.0), 0.0)

        subtract(g1, g2, dst)

        equalizeHist(dst, dst)

        Core.bitwise_not(dst, dst)

        val bmp = convertMatToBitMap(dst)
        return dst
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

}