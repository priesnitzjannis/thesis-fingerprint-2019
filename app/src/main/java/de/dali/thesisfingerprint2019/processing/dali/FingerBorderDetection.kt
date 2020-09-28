package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.logging.Logging
import de.dali.thesisfingerprint2019.processing.Config
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
//import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImageNew
import de.dali.thesisfingerprint2019.processing.Utils.releaseImage
import de.dali.thesisfingerprint2019.processing.Utils.saveImgToDisk
import org.opencv.core.Core
import org.opencv.core.CvType.CV_8UC1
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc.*
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
        Logging.createLogEntry(
            Logging.loggingLevel_param,
            1500,
            "Config data for Finger Border Detection:\nKERNEL_SIZE_BLUR = " + Config.KERNEL_SIZE_BLUR + "\n\nTHRESHOLD_MAX = " + Config.THRESHOLD_MAX + "\nBLOCKSIZE = " + Config.BLOCKSIZE + "\n\nDILATE_KERNEL_SIZE = " + Config.DILATE_KERNEL_SIZE + "\nDILATE_ITERATIONS = " + Config.DILATE_ITERATIONS + "\n\nERODE_KERNEL_SIZE = " + Config.ERODE_KERNEL_SIZE + "\nERODE_ITERATIONS = " + Config.ERODE_ITERATIONS + "\n\nPIXEL_TO_CROP = " + Config.PIXEL_TO_CROP
        )
        val start = System.currentTimeMillis()

        val edgeImage = adaptiveThresh(originalImage)

        val contour =
            measureTimeMillis({ time -> Log.d(QualityAssuranceThread.TAG, "-> getFingerContour:  $time") }) {
                getFingerContour(edgeImage)
            }
        var cnt = 0



        saveImgToDisk(edgeImage, "bar")

        saveImgToDisk(edgeImage, "foo")



        for (i in contour){
            saveImgToDisk(i, "Contour_$cnt$")
            cnt++
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