package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.processing.ProcessingStep
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.RotatedRect
import org.opencv.imgproc.Imgproc
import javax.inject.Inject

class FingerSeparation @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = FingerSeparation::class.java.simpleName

    override fun run(originalImage: Mat): Mat? {
        val sepContours = getContours(originalImage)
        val rois = getROIs(sepContours)
        val croppedImage = getCroppedImage(originalImage, rois)

        return null
    }

    private fun getROIs(mop: List<MatOfPoint>): List<RotatedRect> {
        val listOfRects = mutableListOf<RotatedRect>()

        mop.forEach {
            listOfRects.add(Imgproc.minAreaRect(MatOfPoint2f(it)))
        }

        return listOfRects
    }

    private fun getContours(mat: Mat): List<MatOfPoint> {
        val contours: List<MatOfPoint> = mutableListOf()
        val hierarchy = Mat()

        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        return contours
    }

    private fun getCroppedImage(mat: Mat, rotatedRects: List<RotatedRect>): List<Mat> {

        val result = mutableListOf<Mat>()

        rotatedRects.forEach {
            //result.add(Mat(mat, it))
        }

        return result
    }
}