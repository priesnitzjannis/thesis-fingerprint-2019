package de.dali.thesisfingerprint2019.processing.stein

import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.convertMatToBitMap
import org.opencv.core.*
import org.opencv.core.CvType.CV_8UC1
import org.opencv.imgproc.Imgproc.*
import javax.inject.Inject

class FingerSegmentation @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = FingerSegmentation::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        val lRgb = ArrayList<Mat>(3)
        val imageThresh = Mat.zeros(originalImage.rows(), originalImage.cols(), CV_8UC1)

        Core.split(originalImage, lRgb)

        threshold(lRgb[0], imageThresh, 100.0, 255.0, THRESH_BINARY + THRESH_OTSU)

        var largestArea = 0.0
        val contours: List<MatOfPoint> = mutableListOf()
        var biggestContour = MatOfPoint()

        val hierarchy = Mat()
        findContours(imageThresh, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE)

        contours.forEach {
            val area = contourArea(it, false)
            if (area > largestArea) {
                largestArea = area
                biggestContour = it
            }
        }

        val mask = Mat.zeros(originalImage.rows(), originalImage.cols(), CV_8UC1)
        val wrapper = listOf(biggestContour)
        drawContours(mask, wrapper, -1, Scalar(255.0), FILLED)
        val crop = Mat(Size(originalImage.rows().toDouble(), originalImage.cols().toDouble()), CV_8UC1, Scalar(0.0))
        originalImage.copyTo(crop, mask)

        return crop
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }
}