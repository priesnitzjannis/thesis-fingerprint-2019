package de.dali.thesisfingerprint2019.processing.dali

import android.util.Log
import de.dali.thesisfingerprint2019.processing.Config.PIXEL_TO_CROP
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.QualityAssuranceThread
import de.dali.thesisfingerprint2019.processing.Utils.adaptiveThresh
import de.dali.thesisfingerprint2019.processing.Utils.dilate
import de.dali.thesisfingerprint2019.processing.Utils.erode
import de.dali.thesisfingerprint2019.processing.Utils.fixPossibleDefects
import de.dali.thesisfingerprint2019.processing.Utils.getFingerContour
import de.dali.thesisfingerprint2019.processing.Utils.getMaskImage
import de.dali.thesisfingerprint2019.processing.Utils.getMaskedImage
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImageNew
import de.dali.thesisfingerprint2019.processing.Utils.releaseImage
import org.opencv.core.Core
import org.opencv.core.CvType.CV_8UC1
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.imgproc.Imgproc.boundingRect
import org.opencv.imgproc.Imgproc.moments
import javax.inject.Inject


class FingerBorderDetection @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = FingerBorderDetection::class.java.simpleName

    var amountOfFinger: Int = 0

    override fun run(originalImage: Mat): Mat {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    inline fun <T> measureTimeMillis(loggingFunction: (Long) -> Unit,
                                     function: () -> T): T {

        val startTime = System.currentTimeMillis()
        val result: T = function.invoke()
        loggingFunction.invoke(System.currentTimeMillis() - startTime)

        return result
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        val edgeImage = measureTimeMillis({ time -> Log.d(QualityAssuranceThread.TAG, "-> adaptiveThresh:  $time") }) {
            adaptiveThresh(originalImage)
        }

        var edgesDilated = measureTimeMillis({ time -> Log.d(QualityAssuranceThread.TAG, "-> dilate:  $time") }) {dilate(edgeImage)}
        edgesDilated = measureTimeMillis({ time -> Log.d(QualityAssuranceThread.TAG, "-> erode:  $time") }) {erode(edgesDilated)}

        val thresholdImage = measureTimeMillis({ time -> Log.d(QualityAssuranceThread.TAG, "-> getThresholdImageNew:  $time") }) {getThresholdImageNew(originalImage)}
        val contour = measureTimeMillis({ time -> Log.d(QualityAssuranceThread.TAG, "-> getFingerContour:  $time") }) {getFingerContour(thresholdImage)}

        val maskImage = measureTimeMillis({ time -> Log.d(QualityAssuranceThread.TAG, "-> getMaskImage:  $time") }) {getMaskImage(originalImage, contour)}
        val diffMaskEdge = Mat.zeros(originalImage.rows(), originalImage.cols(), CV_8UC1)
        Core.subtract(maskImage, edgesDilated, diffMaskEdge)

        releaseImage(contour)
        releaseImage(listOf(thresholdImage, edgeImage, edgesDilated, maskImage))

        val newImages = measureTimeMillis({ time -> Log.d(QualityAssuranceThread.TAG, "-> cropPalmIfNeeded:  $time") }) {cropPalmIfNeeded(originalImage, diffMaskEdge, (originalImage.rows() * 0.5).toInt())}

        var sepImages = emptyList<Mat>()

        if (newImages != null) {
            val sepContours = measureTimeMillis({ time -> Log.d(QualityAssuranceThread.TAG, "-> getFingerContour:  $time") }) {getFingerContour(newImages.second).sortedBy { moments(it).m10 / moments(it).m00 }}
            val sepCntFixed = measureTimeMillis({ time -> Log.d(QualityAssuranceThread.TAG, "-> fixPossibleDefects:  $time") }) {sepContours.map { fixPossibleDefects(it, newImages.first) }}

            sepImages = sepCntFixed.mapIndexed { index, mat ->
                val m = measureTimeMillis({ time -> Log.d(QualityAssuranceThread.TAG, "-> getMaskedImage:  $time") }) {getMaskedImage(newImages.first, mat)}
                val r = boundingRect(sepCntFixed[index])
                Mat(m, r)
            }

            releaseImage(sepContours)
            releaseImage(sepCntFixed)
        }

        return sepImages
    }

    private fun cropPalmIfNeeded(orig: Mat, mask: Mat, minSize: Int): Pair<Mat, Mat>? {
        val contour = getFingerContour(mask)

        return if (contour.size == amountOfFinger) {
            Pair(orig, getMaskImage(orig, contour))
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