package de.dali.demonstrator.processing

import android.graphics.Bitmap
import android.util.Log
import de.dali.demonstrator.logging.Logging
import de.dali.demonstrator.processing.Config.BLOCKSIZE
import de.dali.demonstrator.processing.Config.DELTA
import de.dali.demonstrator.processing.Config.DILATE_ITERATIONS
import de.dali.demonstrator.processing.Config.DILATE_KERNEL_SIZE
import de.dali.demonstrator.processing.Config.ERODE_ITERATIONS
import de.dali.demonstrator.processing.Config.ERODE_KERNEL_SIZE
import de.dali.demonstrator.processing.Config.GRAD_X
import de.dali.demonstrator.processing.Config.GRAD_Y
import de.dali.demonstrator.processing.Config.KERNEL_SIZE_BLUR
import de.dali.demonstrator.processing.Config.K_SIZE_SOBEL
import de.dali.demonstrator.processing.Config.SCALE
import de.dali.demonstrator.processing.Config.THRESHOLD_MAX
import org.opencv.core.*
import org.opencv.core.Core.countNonZero
import org.opencv.imgproc.Imgproc.*


object Utils {

    enum class HAND {
        LEFT,
        RIGHT,
        NOT_SPECIFIED
    }

    enum class YCrCb(val channelID: Int) {
        Y(0),
        Cr(1),
        Cb(2)
    }

    fun erode(mat: Mat): Mat {
        //val start = System.currentTimeMillis()
        val anchor = Point(-1.0, -1.0)

        val kernel = getStructuringElement(MORPH_RECT, Size(ERODE_KERNEL_SIZE, ERODE_KERNEL_SIZE))
        erode(mat, mat, kernel, anchor, ERODE_ITERATIONS)
        //val duration = System.currentTimeMillis() - start
        //println("Logging: Erosion: finished in " + duration + "ms")

        return mat
    }

    fun dilate(mat: Mat): Mat {
        //val start = System.currentTimeMillis()
        val anchor = Point(-1.0, -1.0)

        val kernel = getStructuringElement(MORPH_RECT, Size(DILATE_KERNEL_SIZE, DILATE_KERNEL_SIZE))
        dilate(mat, mat, kernel, anchor, DILATE_ITERATIONS)
        //val duration = System.currentTimeMillis() - start
        //println("Logging: Dilatation: finished in " + duration + "ms")

        return mat
    }

    fun sobel(frame: Mat): Mat {
        //val blurred = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        //GaussianBlur(frame, blurred, Size(KERNEL_SIZE_GAUS, KERNEL_SIZE_GAUS), 0.0, 0.0, Core.BORDER_DEFAULT)

        val gray = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        cvtColor(frame, gray, COLOR_RGB2GRAY)

        //releaseImage(listOf(blurred))

        val grad_x = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        val grad_y = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)

        Sobel(
            gray,
            grad_x,
            CvType.CV_16S,
            1,
            0,
            K_SIZE_SOBEL,
            SCALE,
            DELTA,
            Core.BORDER_DEFAULT
        )

        Sobel(
            gray,
            grad_y,
            CvType.CV_16S,
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
        val img_hsv = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
        cvtColor(mat, img_hsv, COLOR_RGB2HSV)
        val lHSV = ArrayList<Mat>(3)
        Core.split(img_hsv, lHSV)

        val result = Mat()

        val blurred = Mat.zeros(mat.rows(), mat.cols(), CvType.CV_64FC1)
        GaussianBlur(lHSV[2], blurred, Size(KERNEL_SIZE_BLUR, KERNEL_SIZE_BLUR), 0.0, 0.0, Core.BORDER_CONSTANT)

        adaptiveThreshold(
            blurred,
            result,
            THRESHOLD_MAX,
            ADAPTIVE_THRESH_MEAN_C,
            THRESH_BINARY_INV,
            BLOCKSIZE,
            12.0
        )
        Logging.createLogEntry(Logging.loggingLevel_critical, 1500, "adaptiveThreshold", result)

        threshold(result, result, 1.0, 255.0, THRESH_BINARY + THRESH_OTSU)
        Logging.createLogEntry(Logging.loggingLevel_critical, 1500, "threshold", result)

        releaseImage(listOf(img_hsv, blurred))

        return result
    }

    fun getFingerContour(mat: Mat): List<MatOfPoint> {

        val contours: List<MatOfPoint> = mutableListOf()
        val hierarchy = Mat()

        findContours(mat, contours, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE)

        return contours.filter {
            val area = contourArea(it, false)
            area >= Config.MIN_AREA_SIZE
        }
    }

    fun getMaskImage(originalImage: Mat, mat: List<MatOfPoint>): Mat {
        val mask = Mat.zeros(originalImage.rows(), originalImage.cols(), CvType.CV_8UC1)
        drawContours(mask, mat, -1, Scalar(255.0), FILLED)

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

        cvtColor(input, rgb, COLOR_BGR2RGBA, 4)

        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888)

            org.opencv.android.Utils.matToBitmap(input, bmp, true)
        } catch (e: CvException) {
            Log.d("Exception", e.message)
        }

        return bmp
    }

      fun getThresholdImageNew(mat: Mat): Mat {
          val img_hsv = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
          val img_mask_h = Mat(mat.rows(), mat.cols(), CvType.CV_8UC1)
          cvtColor(mat, img_hsv, COLOR_RGB2HSV)
          val lHSV = ArrayList<Mat>(3)

          Core.split(img_hsv, lHSV)

          threshold(lHSV[2], img_mask_h, 0.0, 255.0, THRESH_BINARY + THRESH_OTSU)

          return img_mask_h
      }

    fun fixPossibleDefects(contour: MatOfPoint, mat: Mat): Mat {
        val hulls = mutableListOf<MatOfPoint>()

        val hull = MatOfInt()
        convexHull(contour, hull, true)
        hulls.add(hull.toMatOfPoint(contour))

        val result = Mat.zeros(mat.rows(), mat.cols(), CvType.CV_8UC1)
        hulls.forEachIndexed { index, _ ->
            drawContours(result, hulls, index, Scalar(255.0), FILLED)
        }

        return result
    }

    fun releaseImage(mats: List<Mat>) {
        mats.forEach {
            it.release()
        }
    }


    fun rotateImageByDegree(correctionAngle: Double, originalImage: Mat): Mat {
        val center = Point((originalImage.cols() - 1.0) / 2.0, (originalImage.rows() - 1.0) / 2.0)
        val rot = getRotationMatrix2D(center, correctionAngle, 1.0)
        val bbox = RotatedRect(Point(), originalImage.size(), correctionAngle).boundingRect()

        rot.put(0, 2, rot.get(0, 2)[0] + bbox.width / 2.0 - originalImage.cols() / 2.0)
        rot.put(1, 2, rot.get(1, 2)[0] + bbox.height / 2.0 - originalImage.rows() / 2.0)

        val destMat = Mat()
        warpAffine(originalImage, destMat, rot, bbox.size())

        releaseImage(
            listOf(
                rot
            )
        )
        return destMat.cropToMinArea()
    }

    fun hasValidSize(mat: Mat): Boolean {
        return mat.cols() < 333 && mat.cols() > 150 && mat.rows() < 500 && mat.rows() > 225 && mat.rows().toDouble() / mat.cols().toDouble() < 1.75 && (mat.rows().toDouble() / mat.cols().toDouble()) > 1.25
    }

    fun hasEnoughContent(mat: Mat): Boolean {
        val gray = Mat(mat.rows(), mat.cols(), CvType.CV_8UC1)
        cvtColor(mat, gray, COLOR_RGB2GRAY)
        threshold(gray, gray, 200.0, 255.0, THRESH_BINARY + THRESH_OTSU)

        return countNonZero(gray) >= (gray.rows() * gray.cols()) * (3 / 4)
    }

}