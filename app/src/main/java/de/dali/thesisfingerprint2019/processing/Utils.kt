package de.dali.thesisfingerprint2019.processing

import android.graphics.Bitmap
import android.util.Log
import de.dali.thesisfingerprint2019.processing.Config.K_SIZE_GAUS
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.utils.Converters
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.sqrt


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
        val thresh2 = 25.0
        val KERNEL_SIZE = 3

        val gray = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY)

        val blurred = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        Imgproc.GaussianBlur(gray, blurred, Size(13.0, 13.0), 0.0, 0.0, Core.BORDER_CONSTANT)

        releaseImage(listOf(gray))

        Imgproc.Canny(blurred, result, thresh1, thresh2, KERNEL_SIZE, true)

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

    fun Mat.cropToMinArea(): Mat {
        val thresh = getThresholdImage(this)
        val contour = getContour(thresh)
        val rect = Imgproc.boundingRect(contour.toMat())

        return Mat(this, rect)
    }

    fun releaseImage(mats: List<Mat>) {
        mats.forEach {
            it.release()
        }
    }

    fun calcAngle(distanceP1P2: Double, distanceP2ToContour: Double, distanceP1ToContour: Double): Double {
        return atan(distanceP1P2 / (distanceP2ToContour - distanceP1ToContour)) * 180 / PI
    }

    operator fun Point.minus(p: Point) = Point(this.x - p.x, this.y - p.y)

    fun euclideanDist(first: Point, second: Point): Double {
        val diff = first - second
        return sqrt(diff.x * diff.x + diff.y * diff.y)
    }

    fun List<MatOfPoint>.toMat(): Mat {
        val list = mutableListOf<Point>()

        this.forEach {
            list.addAll(it.toList())
        }

        return Converters.vector_Point_to_Mat(list)
    }

    fun calcCenterPoint(originalImage: Mat): Point {
        val pY = originalImage.cols() / 2 - Config.CENTER_OFFSET_X
        val pX = originalImage.rows() / 2 - Config.CENTER_OFFSET_Y

        return Point(pX, pY)
    }

    fun rotateImageByDegree(correctionAngle: Double, originalImage: Mat): Mat {
        val center = Point((originalImage.cols() - 1.0) / 2.0, (originalImage.rows() - 1.0) / 2.0)
        val rot = Imgproc.getRotationMatrix2D(center, correctionAngle, 1.0)
        val bbox = RotatedRect(Point(), originalImage.size(), correctionAngle).boundingRect()

        rot.put(0, 2, rot.get(0, 2)[0] + bbox.width / 2.0 - originalImage.cols() / 2.0)
        rot.put(1, 2, rot.get(1, 2)[0] + bbox.height / 2.0 - originalImage.rows() / 2.0)

        val destMat = Mat()
        Imgproc.warpAffine(originalImage, destMat, rot, bbox.size())

        releaseImage(
            listOf(
                rot
            )
        )

        return destMat.cropToMinArea()
    }

    private fun getContour(mat: Mat): List<MatOfPoint> {
        val contours: List<MatOfPoint> = mutableListOf()
        val hierarchy = Mat()

        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        return contours
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