package de.dali.thesisfingerprint2019.processing

import android.graphics.Bitmap
import android.util.Log
import de.dali.thesisfingerprint2019.processing.Config.BLOCKSIZE
import de.dali.thesisfingerprint2019.processing.Config.DDEPTH
import de.dali.thesisfingerprint2019.processing.Config.DELTA
import de.dali.thesisfingerprint2019.processing.Config.DILATE_ITERATIONS
import de.dali.thesisfingerprint2019.processing.Config.DILATE_KERNEL_SIZE
import de.dali.thesisfingerprint2019.processing.Config.ERODE_ITERATIONS
import de.dali.thesisfingerprint2019.processing.Config.ERODE_KERNEL_SIZE
import de.dali.thesisfingerprint2019.processing.Config.GRAD_X
import de.dali.thesisfingerprint2019.processing.Config.GRAD_Y
import de.dali.thesisfingerprint2019.processing.Config.KERNEL_SIZE_BLUR
import de.dali.thesisfingerprint2019.processing.Config.KERNEL_SIZE_GAUS
import de.dali.thesisfingerprint2019.processing.Config.K_SIZE_SOBEL
import de.dali.thesisfingerprint2019.processing.Config.SCALE
import de.dali.thesisfingerprint2019.processing.Config.THRESHOLD_MAX
import de.dali.thesisfingerprint2019.processing.Utils.HAND.*
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.THRESH_BINARY
import org.opencv.imgproc.Imgproc.THRESH_OTSU
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.sqrt

object Utils {

    enum class HAND {
        LEFT,
        RIGHT,
        NOT_SPECIFIED
    }

    fun erode(mat: Mat): Mat {
        val anchor = Point(-1.0, -1.0)

        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, ERODE_KERNEL_SIZE)
        Imgproc.erode(mat, mat, kernel, anchor, ERODE_ITERATIONS)

        return mat
    }

    fun dilate(mat: Mat): Mat {
        val anchor = Point(-1.0, -1.0)

        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, DILATE_KERNEL_SIZE)
        Imgproc.dilate(mat, mat, kernel, anchor, DILATE_ITERATIONS)

        return mat
    }

    fun sobel(frame: Mat): Mat {
        val blurred = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        Imgproc.GaussianBlur(frame, blurred, KERNEL_SIZE_GAUS, 0.0, 0.0, Core.BORDER_DEFAULT)

        val gray = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        Imgproc.cvtColor(blurred, gray, Imgproc.COLOR_RGB2GRAY)

        releaseImage(listOf(blurred))

        val grad_x = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        val grad_y = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)

        Imgproc.Sobel(
            gray,
            grad_x,
            DDEPTH,
            1,
            0,
            K_SIZE_SOBEL,
            SCALE,
            DELTA,
            Core.BORDER_DEFAULT
        )

        Imgproc.Sobel(
            gray,
            grad_y,
            DDEPTH,
            0,
            1,
            K_SIZE_SOBEL,
            SCALE,
            DELTA,
            Core.BORDER_DEFAULT
        )

        releaseImage(listOf(gray))

        val abs_grad_x = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        val abs_grad_y = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)

        Core.convertScaleAbs(grad_x, abs_grad_x)
        Core.convertScaleAbs(grad_y, abs_grad_y)

        releaseImage(listOf(grad_x, grad_y))

        val result = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)

        Core.addWeighted(abs_grad_x, GRAD_X, abs_grad_y, GRAD_Y, 0.0, result)

        releaseImage(listOf(abs_grad_x, abs_grad_y))

        return result

    }

    fun adaptiveThresh(mat: Mat): Mat {
        val cb = getCbComponent(mat)
        val result = Mat()

        val blurred = Mat.zeros(mat.rows(), mat.cols(), CvType.CV_64FC1)
        Imgproc.GaussianBlur(cb, blurred, KERNEL_SIZE_BLUR, 0.0, 0.0, Core.BORDER_CONSTANT)

        Imgproc.adaptiveThreshold(
            blurred,
            result,
            THRESHOLD_MAX,
            Imgproc.ADAPTIVE_THRESH_MEAN_C,
            Imgproc.THRESH_BINARY_INV,
            BLOCKSIZE,
            12.0
        )

        Imgproc.threshold(result, result, 1.0, 255.0, Imgproc.THRESH_BINARY)

        releaseImage(listOf(cb, blurred))

        return result
    }

    fun getFingerContour(mat: Mat): List<MatOfPoint> {

        val contours: List<MatOfPoint> = mutableListOf()
        val hierarchy = Mat()

        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        return contours.filter {
            val area = Imgproc.contourArea(it, false)
            area >= Config.MIN_AREA_SIZE
        }
    }

    fun getMaskImage(originalImage: Mat, mat: List<MatOfPoint>): Mat {
        val mask = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_8UC1)
        Imgproc.drawContours(mask, mat, -1, Scalar(255.0), Imgproc.FILLED)

        return mask
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

    fun getThresholdImageNew(mat: Mat): Mat{
        val img_hsv = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
        val img_mask_hsv = Mat(mat.rows(), mat.cols(), CvType.CV_8UC1)
        val kernel = Mat(Size(3.0, 3.0), CvType.CV_8UC1, Scalar(255.0))

        Imgproc.cvtColor(mat, img_hsv, Imgproc.COLOR_RGB2HSV)
        Core.inRange(img_hsv, Scalar(0.0, 40.0, 0.0, 0.0), Scalar(25.0, 255.0, 255.0, 0.0), img_mask_hsv)
        Imgproc.morphologyEx(img_mask_hsv, img_mask_hsv, Imgproc.MORPH_OPEN, kernel);

        val img_ycrcb = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
        val img_mask_ycrcb = Mat(mat.rows(), mat.cols(), CvType.CV_8UC1)

        Imgproc.cvtColor(mat, img_ycrcb, Imgproc.COLOR_RGB2YCrCb)
        Core.inRange(img_ycrcb, Scalar(0.0, 138.0,67.0, 0.0), Scalar(255.0, 173.0,133.0, 0.0), img_mask_ycrcb)
        Imgproc.morphologyEx(img_mask_ycrcb, img_mask_ycrcb, Imgproc.MORPH_OPEN, kernel);


        val img_and = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
        val kernel_and = Mat(Size(11.0, 11.0), CvType.CV_8UC1, Scalar(255.0))
        Core.bitwise_and(img_mask_hsv, img_mask_ycrcb, img_and)
        Imgproc.medianBlur(img_and, img_and, 3)
        Imgproc.morphologyEx(img_and, img_and, Imgproc.MORPH_OPEN, kernel_and);

        return img_and
    }

    fun fixPossibleDefects(contour: MatOfPoint, mat: Mat): Mat {
        val hulls = mutableListOf<MatOfPoint>()

        val hull = MatOfInt()
        Imgproc.convexHull(contour, hull, true)
        hulls.add(hull.toMatOfPoint(contour))

        val result = Mat.zeros(mat.rows(), mat.cols(), CvType.CV_8UC1)
        hulls.forEachIndexed { index, _ ->
            Imgproc.drawContours(result, hulls, index, Scalar(255.0), Imgproc.FILLED)
        }

        return result
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

    fun calcAngle(distanceP1P2: Double, distanceP2ToContour: Double, distanceP1ToContour: Double): Double {
        return atan(distanceP1P2 / (distanceP2ToContour - distanceP1ToContour)) * 180 / PI
    }

    fun euclideanDist(first: Point, second: Point): Double {
        val diff = first - second
        return sqrt(diff.x * diff.x + diff.y * diff.y)
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

    fun conditionalPointOnContour(hand: HAND,point: Point, mat: Mat, operator:(i : Int)-> Boolean){
        when(hand){
            NOT_SPECIFIED, LEFT -> for (i in point.x.toInt() until mat.cols()) { if (operator (i)) break }
            RIGHT -> for (i in point.x.toInt() downTo 0) { if (operator (i)) break }
        }
    }

    private fun getCbComponent(mat: Mat): Mat {
        val ycrcb = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
        val lYCrCb = ArrayList<Mat>(3)

        Imgproc.cvtColor(mat, ycrcb, Imgproc.COLOR_BGR2YCrCb)
        Core.split(mat, lYCrCb)

        return lYCrCb[2]
    }

    private fun threshold(mat: Mat): Mat {
        val imageThresh = Mat()
        Imgproc.threshold(mat, imageThresh, 100.0, 255.0, THRESH_BINARY + THRESH_OTSU)

        return imageThresh
    }

}