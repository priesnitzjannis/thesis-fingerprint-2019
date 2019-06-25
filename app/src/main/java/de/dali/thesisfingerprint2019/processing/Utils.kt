package de.dali.thesisfingerprint2019.processing

import android.graphics.Bitmap
import android.util.Log
import de.dali.thesisfingerprint2019.processing.Config.K_SIZE_GAUS
import org.opencv.core.*
import org.opencv.imgproc.Imgproc


object Utils {

    fun erode(
        mat: Mat,
        kernelSize: Size = Size(17.0, 17.0),
        iterations: Int = 2
    ): Mat {
        val anchor = Point(-1.0, -1.0)

        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, kernelSize)
        Imgproc.erode(mat, mat, kernel, anchor, iterations)

        return mat
    }

    fun dilate(
        mat: Mat,
        kernelSize: Size = Size(11.0, 11.0),
        iterations: Int = 2
    ): Mat {
        val anchor = Point(-1.0, -1.0)

        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, kernelSize)
        Imgproc.dilate(mat, mat, kernel, anchor, iterations)

        return mat
    }


    fun getMaskedImage(originalImage: Mat, mask: Mat): Mat {
        val maskedImage = Mat(
            Size(
                originalImage.rows().toDouble(),
                originalImage.cols().toDouble()
            ),
            CvType.CV_8UC1,
            Scalar(0.0)
        )

        originalImage.copyTo(maskedImage, mask)

        return maskedImage
    }

    fun canny(frame: Mat): Mat {
        val result = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        val thresh1 = 15.0
        val thresh2 = 20.0
        val KERNEL_SIZE = 3

        val gray = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY)

        val blurred = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        Imgproc.GaussianBlur(gray, blurred, Size(37.0, 37.0), 0.0, 0.0, Core.BORDER_CONSTANT)

        releaseImage(listOf(gray))

        Imgproc.Canny(blurred, result, thresh1, thresh2, KERNEL_SIZE, false)

        return result
    }

    fun sobel(
        frame: Mat,
        kernelSize: Size? = Size(K_SIZE_GAUS, K_SIZE_GAUS)
    ): Mat {

        val blurred = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        Imgproc.GaussianBlur(frame, blurred, kernelSize, 0.0, 0.0, Core.BORDER_DEFAULT)

        val gray = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        Imgproc.cvtColor(blurred, gray, Imgproc.COLOR_BGR2GRAY)

        releaseImage(listOf(blurred))

        val grad_x = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        val grad_y = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)

        Imgproc.Sobel(
            gray,
            grad_x,
            Config.DDEPTH,
            1,
            0,
            Config.K_SIZE_SOBEL,
            Config.SCALE,
            Config.DELTA,
            Core.BORDER_DEFAULT
        )
        Imgproc.Sobel(
            gray,
            grad_y,
            Config.DDEPTH,
            0,
            1,
            Config.K_SIZE_SOBEL,
            Config.SCALE,
            Config.DELTA,
            Core.BORDER_DEFAULT
        )

        releaseImage(listOf(gray))

        val abs_grad_x = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        val abs_grad_y = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)

        Core.convertScaleAbs(grad_x, abs_grad_x)
        Core.convertScaleAbs(grad_y, abs_grad_y)

        releaseImage(listOf(grad_x, grad_y))

        val result = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)

        Core.addWeighted(abs_grad_x, Config.GRAD_X, abs_grad_y, Config.GRAD_Y, 0.0, result)

        releaseImage(listOf(abs_grad_x, abs_grad_y))

        return result

    }

    fun convertMatToBitMap(input: Mat): Bitmap? {
        var bmp: Bitmap? = null
        val rgb = Mat()

        Imgproc.cvtColor(input, rgb, Imgproc.COLOR_BGR2RGBA, 4)

        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888)

            org.opencv.android.Utils.matToBitmap(input, bmp, true)
        } catch (e: CvException) {
            Log.d("Exception", e.message)
        }

        return bmp
    }

    fun getThresholdImage(mat: Mat): Mat {
        val cb = getCbComponent(mat)
        return threshold(cb)
    }

    fun releaseImage(mats: List<Mat>) {
        mats.forEach {
            it.release()
        }
    }

    fun rotateImageByDegree(correctionAngle: Double, originalImage: Mat): Mat {
        val rotMat: Mat
        val destination = Mat(originalImage.rows(), originalImage.cols(), originalImage.type())
        val center = Point((destination.cols() / 2).toDouble(), (destination.rows() / 2).toDouble())
        rotMat = Imgproc.getRotationMatrix2D(center, correctionAngle, 1.0)
        Imgproc.warpAffine(originalImage, destination, rotMat, destination.size())

        releaseImage(
            listOf(
                rotMat
            )
        )

        return destination
    }

    private fun getCbComponent(mat: Mat): Mat {
        val ycrcb = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
        val lYCrCb = ArrayList<Mat>(3)

        Imgproc.cvtColor(mat, ycrcb, Imgproc.COLOR_RGB2YCrCb)
        Core.split(mat, lYCrCb)

        return lYCrCb[2]
    }

    private fun threshold(mat: Mat): Mat {
        val imageThresh = Mat()
        Imgproc.threshold(mat, imageThresh, 100.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)

        return imageThresh
    }

}