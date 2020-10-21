package de.dali.thesisfingerprint2019.processing.dali

import android.util.Log
import de.dali.thesisfingerprint2019.logging.Logging
import de.dali.thesisfingerprint2019.processing.Config
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.getFingerContour
import de.dali.thesisfingerprint2019.processing.Utils.getMaskImage
import de.dali.thesisfingerprint2019.processing.Utils.getMaskedImage
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImageNew
import de.dali.thesisfingerprint2019.processing.Utils.releaseImage
import de.dali.thesisfingerprint2019.processing.toMat
import de.dali.thesisfingerprint2019.ui.main.fragment.scanning.FingerScanningFragment
import org.opencv.core.CvException
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import javax.inject.Inject


class MultiFingerDetection @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = MultiFingerDetection::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        // If \n stops working use 10.toChar()
        Logging.createLogEntry(
            Logging.loggingLevel_param,
            1400,
            "Config data for Multi Finger Detection:\nKERNEL_SIZE_FILTER = " + Config.KERNEL_SIZE_FILTER + "\n\nH_LOWER = " + Config.H_LOWER + "\nS_LOWER = " + Config.S_LOWER + "\nV_LOWER = " + Config.V_LOWER + "\n\nH_UPPER = " + Config.H_UPPER + "\nS_UPPER = " + Config.S_UPPER + "\nV_UPPER = " + Config.V_UPPER + "\n\nY_LOWER = " + Config.Y_LOWER + "\nCR_LOWER = " + Config.CR_LOWER + "\nCB_LOWER = " + Config.CB_LOWER + "\n\nY_UPPER = " + Config.Y_UPPER + "\nCR_UPPER = " + Config.CR_UPPER + "\nCB_UPPER = " + Config.CB_UPPER + "\n\nKERNEL_SIZE_FAND = " + Config.KERNEL_SIZE_FAND + "\n\nMIN_AREA_SIZE = " + Config.MIN_AREA_SIZE
        )
        val start = System.currentTimeMillis()


        var croppedImage = Mat()

        try {
            val imageThresh = getThresholdImageNew(originalImage)
//            Logging.createLogEntry(
//                Logging.loggingLevel_critical,
//                1400,
//                "Threshold Image",
//                imageThresh
//            )
            val fingerContours = getFingerContour(imageThresh)

            releaseImage(listOf(imageThresh))

            val maskImage = getMaskImage(originalImage, fingerContours)
            val imageWithOutBackground = getMaskedImage(originalImage, maskImage)
//            Logging.createLogEntry(
//                Logging.loggingLevel_critical,
//                1400,
//                "imageWithOutBackground",
//                imageWithOutBackground
//            )

            if (fingerContours.isNotEmpty()) {
                val rect = Imgproc.boundingRect(fingerContours.toMat())

                releaseImage(fingerContours)
                releaseImage(listOf(maskImage))

                croppedImage = Mat(imageWithOutBackground, rect)

                releaseImage(listOf(imageWithOutBackground))

            }

            val duration = System.currentTimeMillis() - start
            Logging.createLogEntry(Logging.loggingLevel_medium, 1400, "Multi Finger Detection finished in " + duration + "ms.")

            if (croppedImage.height() == 0 || croppedImage.width() == 0) {
                Logging.createLogEntry(
                    Logging.loggingLevel_critical,
                    1400,
                    "Multi Finger Detection done, no fingers detected."
                )
            } else {
                // This has commented out code surrounding it that measures the time it takes to log a normal message and an image
                //val startImageMessage = System.currentTimeMillis()
//                Logging.createLogEntry(
//                    Logging.loggingLevel_critical,
//                    1400,
//                    "Multi Finger Detection done, see image for results.",
//                    croppedImage
//                )
                //val imageMessageDuration = System.currentTimeMillis() - startImageMessage
                //val startTextMessage = System.currentTimeMillis()
                //Logging.createLogEntry(
                //    Logging.loggingLevel_critical,
                //    1400,
                //    "Multi Finger Detection done, see image for results."
                //)
                //val textMessageDuration = System.currentTimeMillis() - startTextMessage

                //println("Logging: Image message took " + imageMessageDuration + "ms.")
                //println("Logging: Text message took " + textMessageDuration + "ms.")
            }
        } catch (e: CvException) {
            Log.e(FingerScanningFragment.TAG, "\n\n\n CAUGHT CvException \n\n\n")
            croppedImage = Mat(10, 10, CvType.CV_8U, Scalar.all(0.0))
        }


        return croppedImage

    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }
}