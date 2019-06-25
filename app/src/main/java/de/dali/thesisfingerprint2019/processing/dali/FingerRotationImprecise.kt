package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImage
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc
import javax.inject.Inject
import org.opencv.core.Scalar
import org.opencv.core.Core
import org.opencv.imgproc.Imgproc.line


class FingerRotationImprecise @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = FingerRotationImprecise::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        val thresh = getThresholdImage(originalImage)
        val contour = getFingerContour(thresh)
        val rotated = rotateImprecise(originalImage, contour)

        Utils.releaseImage(contour)
        val a = Utils.convertMatToBitMap(rotated)
        return rotated
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    private fun rotateImprecise(src: Mat, mops: List<MatOfPoint>) : Mat{
        val M: Mat
        val rotated = Mat()
        val cropped = Mat()

        val list = mutableListOf<Point>()

        mops.forEach {
            list.addAll(it.toList())
        }

        val sourceMat = MatOfPoint2f()
        sourceMat.fromList(list)

        val rect = Imgproc.minAreaRect(sourceMat)
        val angle = rect.angle.toFloat()

        M = Imgproc.getRotationMatrix2D(rect.center, angle.toDouble(), 1.0)
        Imgproc.warpAffine(src, rotated, M, src.size(), Imgproc.INTER_CUBIC)
        Imgproc.getRectSubPix(rotated, rect.size, rect.center, cropped)

        return cropped
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
}