package de.dali.thesisfingerprint2019.processing.common

import de.dali.thesisfingerprint2019.processing.Config.CLAHE_ITERATIONS
import de.dali.thesisfingerprint2019.processing.Config.CLIP_LIMIT
import de.dali.thesisfingerprint2019.processing.Config.GAUSSIAN_KERNEL_SIZE_HIGH
import de.dali.thesisfingerprint2019.processing.Config.GAUSSIAN_KERNEL_SIZE_LOW
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.convertMatToBitMap
import org.opencv.core.Core
import org.opencv.core.Core.subtract
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc.*
import javax.inject.Inject
import kotlin.math.pow

class Enhancement @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = Enhancement::class.java.simpleName

    override fun run(originalImage: Mat): Mat {

        for (i in 0 until CLAHE_ITERATIONS) {
            val kernelSize = 2.0.pow(CLAHE_ITERATIONS)/(i + 1)
            val clahe = createCLAHE(CLIP_LIMIT, Size(kernelSize, kernelSize))
            clahe.apply(originalImage, originalImage)
        }

        val g1 = Mat()
        val g2 = Mat()
        val dst = Mat()

        GaussianBlur(originalImage, g1, Size(GAUSSIAN_KERNEL_SIZE_LOW, GAUSSIAN_KERNEL_SIZE_LOW), 0.0)
        GaussianBlur(originalImage, g2, Size(GAUSSIAN_KERNEL_SIZE_HIGH, GAUSSIAN_KERNEL_SIZE_HIGH), 0.0)

        subtract(g1, g2, dst)

        equalizeHist(dst, dst)

        Core.bitwise_not(dst, dst)

        return dst
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

}