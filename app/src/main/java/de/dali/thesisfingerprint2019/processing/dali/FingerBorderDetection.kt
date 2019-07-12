package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.processing.Config.PIXEL_TO_CROP
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.adaptiveThresh
import de.dali.thesisfingerprint2019.processing.Utils.convertMatToBitMap
import de.dali.thesisfingerprint2019.processing.Utils.dilate
import de.dali.thesisfingerprint2019.processing.Utils.erode
import de.dali.thesisfingerprint2019.processing.Utils.getFingerContour
import de.dali.thesisfingerprint2019.processing.Utils.getMaskImage
import de.dali.thesisfingerprint2019.processing.Utils.getMaskedImage
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImage
import de.dali.thesisfingerprint2019.processing.Utils.releaseImage
import de.dali.thesisfingerprint2019.processing.toMatOfPoint
import org.opencv.core.*
import org.opencv.core.CvType.CV_8UC1
import org.opencv.core.CvType.CV_8UC3
import org.opencv.imgproc.Imgproc.*
import javax.inject.Inject
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc




class FingerBorderDetection @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = FingerBorderDetection::class.java.simpleName

    var amountOfFinger: Int = 0

    override fun run(originalImage: Mat): Mat {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        val edgeImage = adaptiveThresh(originalImage)
        var edgesDilated = dilate(edgeImage)
        edgesDilated = erode(edgesDilated)

        val thresholdImage = getThresholdImage(originalImage)
        val contour = getFingerContour(thresholdImage)

        val maskImage = getMaskImage(originalImage, contour)
        val diffMaskEdge = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_8UC1)
        Core.subtract(maskImage, edgesDilated, diffMaskEdge)

        val bmpResult = convertMatToBitMap(diffMaskEdge)

        releaseImage(contour)
        releaseImage(listOf(thresholdImage, edgeImage, edgesDilated, maskImage))

        val newImages = cropPalmIfNeeded(originalImage, diffMaskEdge, (originalImage.rows() * 0.5).toInt())

        var sepImages = emptyList<Mat>()

        if (newImages != null) {
            val sepContours = getFingerContour(newImages.second).sortedByDescending { moments(it).m10 / moments(it).m00 }
            val sepCntFixed = sepContours.map { fixPossibleDefects(it, newImages.first) }

            sepImages = sepCntFixed.mapIndexed { index, mat ->
                val m = getMaskedImage(newImages.first, mat)
                val r = boundingRect(sepCntFixed[index])
                Mat(m, r)
            }

            releaseImage(sepContours)
            releaseImage(sepCntFixed)

            val bmpResult = convertMatToBitMap(sepImages[0])
        }

        releaseImage(listOf(edgeImage))

        return sepImages
    }

    private fun fixPossibleDefects(contour: MatOfPoint, mat: Mat): Mat{
        val hulls = mutableListOf<MatOfPoint>()

        val hull = MatOfInt()
        convexHull(contour, hull, true)
        hulls.add(hull.toMatOfPoint(contour))

        val result = Mat.zeros(mat.rows(), mat.cols(), CV_8UC1)
        hulls.forEachIndexed { index, _ ->
            drawContours(result, hulls, index, Scalar(255.0, 255.0, 255.0), -1)
        }

        return result
    }

    private fun cropPalmIfNeeded(orig: Mat, mask: Mat, minSize: Int): Pair<Mat, Mat>? {
        val contour = getFingerContour(mask)

        return if (contour.size == amountOfFinger) {
            Pair(orig, mask)
        } else {
            val newWidth = orig.cols()
            val newHeight = orig.rows() - PIXEL_TO_CROP

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
}