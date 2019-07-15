package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.convertMatToBitMap
import de.dali.thesisfingerprint2019.processing.Utils.getFingerContour
import de.dali.thesisfingerprint2019.processing.Utils.getMaskImage
import de.dali.thesisfingerprint2019.processing.Utils.getMaskedImage
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImage
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImageNew
import de.dali.thesisfingerprint2019.processing.Utils.releaseImage
import de.dali.thesisfingerprint2019.processing.toMat
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import javax.inject.Inject

class MultiFingerDetection @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = MultiFingerDetection::class.java.simpleName

    override fun run(originalImage: Mat): Mat {

        val imageThresh = getThresholdImageNew(originalImage)
        val fingerContours = getFingerContour(imageThresh)

        releaseImage(listOf(imageThresh))

        val maskImage = getMaskImage(originalImage, fingerContours)
        val imageWithOutBackground = getMaskedImage(originalImage, maskImage)

        val bmpResultABC = convertMatToBitMap(imageWithOutBackground)
        val bmpResultAC = convertMatToBitMap(maskImage)

        var croppedImage = Mat()

        if (fingerContours.isNotEmpty()) {
            val rect = Imgproc.boundingRect(fingerContours.toMat())

            releaseImage(fingerContours)
            releaseImage(listOf(maskImage))

            croppedImage = Mat(imageWithOutBackground, rect)

            releaseImage(listOf(imageWithOutBackground))
        }

        val bmpResult = convertMatToBitMap(croppedImage)
         return croppedImage

    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }
}