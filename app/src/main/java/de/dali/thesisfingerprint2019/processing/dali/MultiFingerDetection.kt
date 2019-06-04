package de.dali.thesisfingerprint2019.processing.dali

import android.os.Environment
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.getMaskedImage
import de.dali.thesisfingerprint2019.utils.Constants
import de.dali.thesisfingerprint2019.utils.Utils
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import javax.inject.Inject

class MultiFingerDetection @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = MultiFingerDetection::class.java.simpleName

    override fun run(originalImage: Mat): Mat? {
        val pathname = "${Environment.getExternalStorageDirectory()}/${Constants.NAME_MAIN_FOLDER}/test/1.jpg"
        val m = Imgcodecs.imread(pathname)

        val cb = getCbComponent(m)
        val imageThresh = getThresholdImage(cb)

        Utils.releaseImage(listOf(cb))

        val biggestContour = getBiggestContour(imageThresh)

        Utils.releaseImage(listOf(imageThresh))

        val maskImage = getMaskImage(m, biggestContour)

        val imageWithOutBackground = getMaskedImage(m, maskImage)
        val rect = Imgproc.boundingRect(biggestContour)

        Utils.releaseImage(listOf(biggestContour))

        val croppedImage = Mat(imageWithOutBackground, rect)

        Utils.releaseImage(listOf(imageWithOutBackground))

        return croppedImage

    }

    private fun getCbComponent(mat: Mat): Mat {
        val ycrcb = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
        val lYCrCb = ArrayList<Mat>(3)

        Imgproc.cvtColor(mat, ycrcb, Imgproc.COLOR_RGB2YCrCb)
        Core.split(mat, lYCrCb)

        return lYCrCb[2]
    }

    private fun getThresholdImage(mat: Mat): Mat {
        val imageThresh = Mat.zeros(mat.rows(), mat.cols(), CvType.CV_8UC1)
        Imgproc.threshold(mat, imageThresh, 100.0, 255.0, Imgproc.THRESH_BINARY)

        return imageThresh
    }

    private fun getBiggestContour(mat: Mat): MatOfPoint {

        var largestArea = 0.0
        val contours: List<MatOfPoint> = mutableListOf()
        var biggestContour = MatOfPoint()

        val hierarchy = Mat()
        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        contours.forEach {
            val area = Imgproc.contourArea(it, false)
            if (area > largestArea) {
                largestArea = area
                biggestContour = it
            }
        }

        return biggestContour
    }

    private fun getMaskImage(originalImage: Mat, mat: MatOfPoint): Mat {
        val mask = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_8UC1)
        val wrapper = listOf(mat)
        Imgproc.drawContours(mask, wrapper, -1, Scalar(255.0), Imgproc.FILLED)

        return mask
    }
}