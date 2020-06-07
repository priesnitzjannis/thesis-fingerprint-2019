package de.dali.thesisfingerprint2019.processing.dali

import android.util.Log
import de.dali.thesisfingerprint2019.logging.Logging
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
        val start = System.currentTimeMillis()


        var croppedImage = Mat()

        try {
            val imageThresh = getThresholdImageNew(originalImage)
            val fingerContours = getFingerContour(imageThresh)

            releaseImage(listOf(imageThresh))

            val maskImage = getMaskImage(originalImage, fingerContours)
            val imageWithOutBackground = getMaskedImage(originalImage, maskImage)


            if (fingerContours.isNotEmpty()) {
                val rect = Imgproc.boundingRect(fingerContours.toMat())

                releaseImage(fingerContours)
                releaseImage(listOf(maskImage))

                croppedImage = Mat(imageWithOutBackground, rect)

                releaseImage(listOf(imageWithOutBackground))

            }

            val duration = System.currentTimeMillis() - start
            Logging.createLogEntry(Logging.loggingLevel_detailed, 1200, "Multi Finger Detection finished in " + duration + "ms.")

            if (croppedImage.height() == 0 || croppedImage.width() == 0) {
                Logging.createLogEntry(
                    Logging.loggingLevel_critical,
                    1200,
                    "Multi Finger Detection done, no fingers detected."
                )
            } else {
                Logging.createLogEntry(
                    Logging.loggingLevel_critical,
                    1200,
                    "Multi Finger Detection done, see image for results.",
                    croppedImage
                )
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