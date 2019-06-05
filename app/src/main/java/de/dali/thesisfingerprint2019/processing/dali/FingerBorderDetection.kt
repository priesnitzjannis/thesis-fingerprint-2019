package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.canny
import de.dali.thesisfingerprint2019.processing.Utils.dilate
import de.dali.thesisfingerprint2019.processing.Utils.erode
import de.dali.thesisfingerprint2019.processing.Utils.getMaskedImage
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImage
import de.dali.thesisfingerprint2019.processing.Utils.sobel
import de.dali.thesisfingerprint2019.utils.Utils
import de.dali.thesisfingerprint2019.utils.Utils.releaseImage
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import javax.inject.Inject
import org.opencv.core.Scalar
import org.opencv.core.CvType
import org.opencv.core.Mat



class FingerBorderDetection @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = FingerBorderDetection::class.java.simpleName

    override fun run(originalImage: Mat): Mat? {

        val bmpOrg = Utils.convertMatToBitMap(originalImage)

        val edgeImage = canny(originalImage)

        val bmpOrg0 = Utils.convertMatToBitMap(edgeImage)

        val kernel = Mat(Size(37.0, 37.0), CvType.CV_8UC1, Scalar(255.0))
        var edgesDilated = Mat.zeros(edgeImage.rows(), edgeImage.cols(), CvType.CV_8UC1)
        Imgproc.morphologyEx(edgeImage, edgesDilated, Imgproc.MORPH_CROSS, kernel)

        edgesDilated = erode(edgesDilated)

        val thresholdImage = getThresholdImage(originalImage)
        val contour = getContour(thresholdImage)[0]
        val maskImage = getMaskImage(originalImage, contour)

        val bmpOrg11 = Utils.convertMatToBitMap(maskImage)
        val bmpOrg1 = Utils.convertMatToBitMap(edgesDilated)

        val diffMaskEdge = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_8UC1)
        Core.subtract(maskImage, edgesDilated, diffMaskEdge)

        releaseImage(listOf(contour, thresholdImage, edgeImage, edgesDilated, maskImage))
        val bmpOrg2 = Utils.convertMatToBitMap(diffMaskEdge)

        val sepContours = getContour(diffMaskEdge)
        val sepContoursImages = sepContours.map { getMaskImage(originalImage, it) }
        val sepImages = sepContoursImages.mapIndexed{ index, mat ->
            val m = getMaskedImage(originalImage, mat)
            val r = Imgproc.boundingRect(sepContoursImages[index])
            Mat(m, r)
        }

        val bmpOrg3 = Utils.convertMatToBitMap(sepImages[0])

        releaseImage(listOf(edgeImage))
        releaseImage(sepContours)
        releaseImage(sepContoursImages)

        return sepImages[0]
    }

    private fun getContour(mat: Mat): List<MatOfPoint> {
        val contours: List<MatOfPoint> = mutableListOf()
        val hierarchy = Mat()

        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        return contours
    }

    private fun getMaskImage(originalImage: Mat, mat: MatOfPoint): Mat {
        val mask = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_8UC1)
        val wrapper = listOf(mat)
        Imgproc.drawContours(mask, wrapper, -1, Scalar(255.0), Imgproc.FILLED)

        return mask
    }
}