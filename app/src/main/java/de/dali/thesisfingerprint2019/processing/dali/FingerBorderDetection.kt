package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.logging.Logging
import de.dali.thesisfingerprint2019.processing.Config
import de.dali.thesisfingerprint2019.processing.Config.PIXEL_TO_CROP
import de.dali.thesisfingerprint2019.processing.ProcessingStep
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

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        Logging.createLogEntry(
            Logging.loggingLevel_param,
            1500,
            "Config data for Finger Border Detection:\nKERNEL_SIZE_BLUR = " + Config.KERNEL_SIZE_BLUR + "\n\nTHRESHOLD_MAX = " + Config.THRESHOLD_MAX + "\nBLOCKSIZE = " + Config.BLOCKSIZE + "\n\nDILATE_KERNEL_SIZE = " + Config.DILATE_KERNEL_SIZE + "\nDILATE_ITERATIONS = " + Config.DILATE_ITERATIONS + "\n\nERODE_KERNEL_SIZE = " + Config.ERODE_KERNEL_SIZE + "\nERODE_ITERATIONS = " + Config.ERODE_ITERATIONS + "\n\nPIXEL_TO_CROP = " + Config.PIXEL_TO_CROP
        )
        val start = System.currentTimeMillis()
        Logging.createLogEntry(Logging.loggingLevel_critical, 1500, "runReturnMultiple input image", originalImage)

        //val edgeImage = adaptiveThresh(originalImage)
        //Logging.createLogEntry(Logging.loggingLevel_critical, 1500, "edgeImage", edgeImage)

        //var edgesDilated = dilate(edgeImage)
        //edgesDilated = erode(edgesDilated)

        val thresholdImage = getThresholdImageNew(originalImage)
        //val contour = getFingerContour(thresholdImage)

        //val maskImage = getMaskImage(originalImage, contour)
        val diffMaskEdge = Mat.zeros(originalImage.rows(), originalImage.cols(), CV_8UC1)
        Core.subtract(thresholdImage, edgeImage, diffMaskEdge)

        //releaseImage(contour)
        releaseImage(listOf(thresholdImage, edgeImage)) //maskImage

        //val diffMaskEdge = Mat.zeros(originalImage.rows(), originalImage.cols(), CV_8UC1)
        //Core.subtract(thresholdImage, edgesDilated, diffMaskEdge)
        //Logging.createLogEntry(Logging.loggingLevel_critical, 1500, "diffMaskEdge", diffMaskEdge)

        //releaseImage(listOf(thresholdImage) + contour) //edgeImage, edgesDilated

        val newImages = cropPalmIfNeeded(originalImage, thresholdImage, (originalImage.rows() * 0.5).toInt())

        var sepImages = emptyList<Mat>()

        if (newImages != null) {
            val sepContours = getFingerContour(newImages.second).sortedBy { moments(it).m10 / moments(it).m00 }
            val sepCntFixed = sepContours.map { fixPossibleDefects(it, newImages.first) }

            sepImages = sepCntFixed.mapIndexed { index, mat ->
                val m = getMaskedImage(newImages.first, mat)
                val r = boundingRect(sepCntFixed[index])
                Mat(m, r)
            }

            releaseImage(sepContours)
            releaseImage(sepCntFixed)
        }


        val duration = System.currentTimeMillis() - start
        Logging.createLogEntry(Logging.loggingLevel_critical, 1500, "Detected " + sepImages.size + " fingers")
        Logging.createLogEntry(Logging.loggingLevel_medium, 1500, "Finger Border Detection finished in " + duration + "ms.")

        sepImages.forEach{
            Logging.createLogEntry(Logging.loggingLevel_medium,1500,"A finger has been detected, see image for result.", it)
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