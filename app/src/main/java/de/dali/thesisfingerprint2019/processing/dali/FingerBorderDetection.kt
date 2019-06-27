package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.adaptiveThresh
import de.dali.thesisfingerprint2019.processing.Utils.canny
import de.dali.thesisfingerprint2019.processing.Utils.convertMatToBitMap
import de.dali.thesisfingerprint2019.processing.Utils.dilate
import de.dali.thesisfingerprint2019.processing.Utils.erode
import de.dali.thesisfingerprint2019.processing.Utils.getMaskedImage
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImage
import de.dali.thesisfingerprint2019.processing.Utils.releaseImage
import org.opencv.core.*
import org.opencv.imgproc.Imgproc.*
import javax.inject.Inject


class FingerBorderDetection @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = FingerBorderDetection::class.java.simpleName

    var amountOfFinger: Int = 0

    override fun run(originalImage: Mat): Mat {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        val edgeImage = adaptiveThresh(originalImage)//canny(originalImage)
        var edgesDilated = dilate(edgeImage, Size(27.0, 27.0))
        edgesDilated = erode(edgesDilated, Size(23.0, 23.0))


        val thresholdImage = getThresholdImage(originalImage)
        val contour = getContour(thresholdImage)

        contour.sortedBy { moments(it).m10 / moments(it).m00 }

        val maskImage = getMaskImage(originalImage, contour)
        val diffMaskEdge = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_8UC1)
        Core.subtract(maskImage, edgesDilated, diffMaskEdge)

        val bmpResultABC = convertMatToBitMap(diffMaskEdge)

        releaseImage(contour)
        releaseImage(listOf(thresholdImage, edgeImage, edgesDilated, maskImage))

        val newImages = cropPalmIfNeeded(originalImage, diffMaskEdge, (originalImage.rows() * 0.5).toInt())

        val sepContours = getContour(newImages!!.second)
        val sepContoursImages = sepContours.map { getMaskImage(newImages.first, listOf(it)) }
        val sepImages = sepContoursImages.mapIndexed { index, mat ->
            val m = getMaskedImage(newImages.first, mat)
            val r = boundingRect(sepContoursImages[index])
            Mat(m, r)
        }

        releaseImage(listOf(edgeImage))
        releaseImage(sepContours)
        releaseImage(sepContoursImages)

        val bmpResult = convertMatToBitMap(sepImages[0])
        return sepImages
    }

    private fun cropPalmIfNeeded(orig: Mat, mask: Mat, minSize: Int): Pair<Mat, Mat>? {
        val contour = getContour(mask)

        return if (contour.size == amountOfFinger) {
            Pair(orig, mask)
        } else {
            val newWidth = orig.cols()
            val newHeight = orig.rows() - 100

            if (minSize < newHeight) {
                val rect = Rect(0, 0, newWidth, newHeight)

                val newOrig = Mat(orig, rect)
                val newMask = Mat(mask, rect)
                cropPalmIfNeeded(newOrig, newMask, minSize)
            } else {
                null
            }
        }
    }

    private fun getContour(mat: Mat): List<MatOfPoint> {
        var contours: List<MatOfPoint> = mutableListOf()
        val hierarchy = Mat()

        findContours(mat, contours, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE)

        contours = contours.filter { contourArea(it) != 0.0 }

        return contours
    }

    private fun getMaskImage(originalImage: Mat, mat: List<MatOfPoint>): Mat {
        val mask = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_8UC1)
        drawContours(mask, mat, -1, Scalar(255.0), FILLED)

        return mask
    }
}