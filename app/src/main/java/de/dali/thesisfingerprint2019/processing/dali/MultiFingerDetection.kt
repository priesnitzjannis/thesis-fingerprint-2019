package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.convertMatToBitMap
import de.dali.thesisfingerprint2019.processing.Utils.getMaskedImage
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImage
import de.dali.thesisfingerprint2019.processing.Utils.releaseImage
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.utils.Converters.vector_Point_to_Mat
import javax.inject.Inject

class MultiFingerDetection @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = MultiFingerDetection::class.java.simpleName

    override fun run(originalImage: Mat): Mat {

        val imageThresh = getThresholdImage(originalImage)
        val bmpOrg1 = convertMatToBitMap(imageThresh)

        val fingerContours = getFingerContour(imageThresh)

        releaseImage(listOf(imageThresh))

        val maskImage = getMaskImage(originalImage, fingerContours)
        val bmpOrg2 = convertMatToBitMap(maskImage)

        val imageWithOutBackground = getMaskedImage(originalImage, maskImage)
        val rect = Imgproc.boundingRect(fingerContours.toMat())
        val bmpOrg3 = convertMatToBitMap(imageWithOutBackground)

        releaseImage(fingerContours)

        val croppedImage = Mat(imageWithOutBackground, rect)
        val bmpOrg4 = convertMatToBitMap(croppedImage)

        releaseImage(listOf(imageWithOutBackground))

        return croppedImage

    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    private fun List<MatOfPoint>.toMat(): Mat {
        val list = mutableListOf<Point>()

        this.forEach {
            list.addAll(it.toList())
        }

        return vector_Point_to_Mat(list)
    }

    private fun getFingerContour(mat: Mat): List<MatOfPoint> {

        val contours: List<MatOfPoint> = mutableListOf()
        val fingerContours = mutableListOf<MatOfPoint>()
        val hierarchy = Mat()

        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        contours.forEach {
            val area = Imgproc.contourArea(it, false)
            if (area >= 400) {
                fingerContours.add(it)
            }
        }

        return fingerContours
    }

    private fun getMaskImage(originalImage: Mat, mat: List<MatOfPoint>): Mat {
        val mask = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_8UC1)
        Imgproc.drawContours(mask, mat, -1, Scalar(255.0), Imgproc.FILLED)

        return mask
    }
}