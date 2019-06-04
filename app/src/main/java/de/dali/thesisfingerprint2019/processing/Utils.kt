package de.dali.thesisfingerprint2019.processing

import de.dali.thesisfingerprint2019.processing.Config.K_SIZE_GAUS
import de.dali.thesisfingerprint2019.utils.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc


object Utils {

    fun erode(
        mat: Mat,
        kernelSize: Size = Size(11.0, 11.0),
        iterations: Int = 2
    ): Mat {
        val anchor = Point(-1.0, -1.0)

        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, kernelSize)
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
        Imgproc.GaussianBlur(gray, blurred, Size(33.0, 33.0), 0.0, 0.0, Core.BORDER_DEFAULT)


        Utils.releaseImage(listOf(gray))

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

        Utils.releaseImage(listOf(blurred))

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

        Utils.releaseImage(listOf(gray))

        val abs_grad_x = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        val abs_grad_y = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)

        Core.convertScaleAbs(grad_x, abs_grad_x)
        Core.convertScaleAbs(grad_y, abs_grad_y)

        Utils.releaseImage(listOf(grad_x, grad_y))

        val result = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)

        Core.addWeighted(abs_grad_x, Config.GRAD_X, abs_grad_y, Config.GRAD_Y, 0.0, result)

        Utils.releaseImage(listOf(abs_grad_x, abs_grad_y))

        return result

    }


    fun getThresholdImage(mat: Mat): Mat {
        val cb = getCbComponent(mat)
        return threshold(cb)
    }

    private fun getCbComponent(mat: Mat): Mat {
        val ycrcb = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
        val lYCrCb = ArrayList<Mat>(3)

        Imgproc.cvtColor(mat, ycrcb, Imgproc.COLOR_RGB2YCrCb)
        Core.split(mat, lYCrCb)

        return lYCrCb[2]
    }

    private fun threshold(mat: Mat): Mat {
        val imageThresh = Mat.zeros(mat.rows(), mat.cols(), CvType.CV_8UC1)
        Imgproc.threshold(mat, imageThresh, 100.0, 255.0, Imgproc.THRESH_BINARY)

        return imageThresh
    }

}