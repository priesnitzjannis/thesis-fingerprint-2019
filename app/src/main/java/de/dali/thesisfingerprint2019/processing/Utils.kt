package de.dali.thesisfingerprint2019.processing

import android.graphics.Bitmap
import android.util.Log
import de.dali.thesisfingerprint2019.processing.Config.DELTA
import de.dali.thesisfingerprint2019.processing.Config.ERODE_ITERATIONS
import de.dali.thesisfingerprint2019.processing.Config.ERODE_KERNEL_SIZE
import de.dali.thesisfingerprint2019.processing.Config.GRAD_X
import de.dali.thesisfingerprint2019.processing.Config.GRAD_Y
import de.dali.thesisfingerprint2019.processing.Config.KERNEL_SIZE_GAUS
import de.dali.thesisfingerprint2019.processing.Config.K_SIZE_SOBEL
import de.dali.thesisfingerprint2019.processing.Config.SCALE
import de.dali.thesisfingerprint2019.processing.Utils.HAND.*
import de.dali.thesisfingerprint2019.processing.Utils.YCrCb.Cb
import de.dali.thesisfingerprint2019.utils.Constants.NAME_MAIN_FOLDER
import de.dali.thesisfingerprint2019.utils.Utils
import org.opencv.core.*
import org.opencv.core.Core.countNonZero
import org.opencv.core.CvType.CV_8UC1
import org.opencv.imgproc.Imgproc.*
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.sqrt

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

    inline fun <T> measureTimeMillis(loggingFunction: (Long) -> Unit,
                                     function: () -> T): T {

        val startTime = System.currentTimeMillis()
        val result: T = function.invoke()
        loggingFunction.invoke(System.currentTimeMillis() - startTime)

        return result
    }

    fun erode(mat: Mat): Mat {
        val anchor = Point(-1.0, -1.0)

        val kernel = getStructuringElement(MORPH_CROSS, Size(ERODE_KERNEL_SIZE, ERODE_KERNEL_SIZE))
        erode(mat, mat, kernel, anchor, ERODE_ITERATIONS)

        return mat
    }

    fun dilate(mat: Mat): Mat {
        val anchor = Point(-1.0, -1.0)

        //val kernel = measureTimeMillis({ time -> Log.d(QualityAssuranceThread.TAG, "--> getStructuringElement:  $time") }) {getStructuringElement(MORPH_ELLIPSE, Size(DILATE_KERNEL_SIZE, DILATE_KERNEL_SIZE))}
        //measureTimeMillis({ time -> Log.d(QualityAssuranceThread.TAG, "--> dilate:  $time") }) {
        //    dilate(mat, mat, kernel, anchor, DILATE_ITERATIONS)
        //}

        return mat
    }

    fun sobel(frame: Mat): Mat {
        val blurred = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        GaussianBlur(frame, blurred, Size(KERNEL_SIZE_GAUS, KERNEL_SIZE_GAUS), 0.0, 0.0, Core.BORDER_DEFAULT)

        val gray = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_64FC1)
        cvtColor(blurred, gray, COLOR_RGB2GRAY)

        releaseImage(listOf(blurred))

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
//        val y = getYCbCRComponent(mat, Y)
//        val ycrcb = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
//        val lYCrCb = ArrayList<Mat>(3)
//
//        cvtColor(mat, ycrcb, COLOR_BGR2YCrCb)
//        Core.split(mat, lYCrCb)

        val result = Mat(mat.rows(), mat.cols(), CvType.CV_8UC1)

        //val blurred = Mat.zeros(mat.rows(), mat.cols(), CvType.CV_64FC1)
        //GaussianBlur(y, blurred, Size(KERNEL_SIZE_BLUR, KERNEL_SIZE_BLUR), 0.0, 0.0, Core.BORDER_CONSTANT)
//
//        adaptiveThreshold(
//            y,
//            result,
//            THRESHOLD_MAX,
//            ADAPTIVE_THRESH_MEAN_C,
//            THRESH_BINARY_INV,
//            BLOCKSIZE,
//            12.0
//        )
        val gray = Mat(mat.rows(), mat.cols(), CvType.CV_8UC1)

        cvtColor(mat, gray, COLOR_RGB2GRAY)

        threshold(gray, result, 0.0, 255.0, THRESH_BINARY + THRESH_OTSU)

        //releaseImage(listOf(y)) //, blurred))

        return result
    }

    fun getFingerContour(mat: Mat): List<MatOfPoint> {

        val contours: List<MatOfPoint> = mutableListOf()
        val hierarchy = Mat()
        var type = mat.type()
        var dims = mat.dims()
        Log.d("IMAGE_PROC: ", "Type: $type$")
        Log.d("IMAGE_PROC: ", "Dims: $dims$")

        if (mat.type() != CV_8UC1){
            cvtColor(mat, mat, COLOR_BGR2GRAY, 2)
        }
        type = mat.type()
        dims = mat.dims()
        Log.d("IMAGE_PROC: ", "Type: $type$")
        Log.d("IMAGE_PROC: ", "Dims: $dims$")
        findContours(mat, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE) //RETR_EXTERNAL

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

    fun secure_cvtColor(src: Mat, dst: Mat, code: Int, dstCn: Int): Mat {
        if (src.dims() == 2) {
            cvtColor(src, src, COLOR_GRAY2BGR, 3)
        }
        cvtColor(src, dst, code, dstCn)
        return dst
    }

    fun convertMatToBitMap(input: Mat): Bitmap? {
        var bmp: Bitmap? = null
        val rgb = Mat()

        secure_cvtColor(input, rgb, COLOR_BGR2RGBA, 4)

        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888)

            org.opencv.android.Utils.matToBitmap(input, bmp, true)
        } catch (e: CvException) {
            Log.d("Exception", e.message)
        }

        return bmp
    }

//        fun getThresholdImageNew(mat: Mat): Mat {
//        //val img_hsv = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
//        //val img_mask_hsv = Mat(mat.rows(), mat.cols(), CvType.CV_8UC1)
//        //val kernel = getStructuringElement(MORPH_ELLIPSE, Size(KERNEL_SIZE_FILTER, KERNEL_SIZE_FILTER))
//
//        //cvtColor(mat, img_hsv, COLOR_RGB2HSV)
//        //Core.inRange(img_hsv, Scalar(H_LOWER, S_LOWER, V_LOWER), Scalar(H_UPPER, S_UPPER, V_UPPER), img_mask_hsv)
//        //morphologyEx(img_mask_hsv, img_mask_hsv, MORPH_OPEN, kernel)
//
////        val img_ycrcb = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
////        val img_mask_ycrcb = Mat(mat.rows(), mat.cols(), CvType.CV_8UC1)
////
////        cvtColor(mat, img_ycrcb, COLOR_RGB2YCrCb)
////        Core.inRange(
////            img_ycrcb,
////            Scalar(Y_LOWER, CR_LOWER, CB_LOWER),
////            Scalar(Y_UPPER, CR_UPPER, CB_UPPER),
////            img_mask_ycrcb
////        )
//        //morphologyEx(img_mask_ycrcb, img_mask_ycrcb, MORPH_OPEN, kernel)
//
//
//        //val img_and = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
//        //val kernel_and = getStructuringElement(MORPH_ELLIPSE, Size(KERNEL_SIZE_FAND, KERNEL_SIZE_FAND))
//        //Core.bitwise_and(img_mask_hsv, img_mask_ycrcb, img_and)
//        //morphologyEx(img_and, img_and, MORPH_CLOSE, kernel_and)
//
//        return img_mask_ycrcb//img_and
//    }

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

    fun getThresholdImage(mat: Mat): Mat {
        val cb = getYCbCRComponent(mat, Cb)
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

    fun hasValidSize(mat: Mat): Boolean = mat.cols() < 400 && mat.rows() < 540 && mat.cols() < mat.rows()

    fun hasEnoughContent(mat: Mat): Boolean {
        val gray = Mat(mat.rows(), mat.cols(), CvType.CV_8UC1)
        cvtColor(mat, gray, COLOR_RGB2GRAY)
        threshold(gray, gray, 200.0, 255.0, THRESH_BINARY + THRESH_OTSU)

        return countNonZero(gray) >= (gray.rows() * gray.cols()) * (3 / 4)
    }

    fun conditionalPointOnContour(hand: HAND, point: Point, mat: Mat, operator: (i: Int) -> Boolean) {
        when (hand) {
            NOT_SPECIFIED, LEFT -> for (i in point.x.toInt() downTo 1) {
                if (operator(i)) break
            }
            RIGHT -> for (i in point.x.toInt() until mat.cols()) {
                if (operator(i)) break
            }
        }
    }

    private fun getYCbCRComponent(mat: Mat, component: YCrCb): Mat {
        val ycrcb = Mat(mat.rows(), mat.cols(), CvType.CV_8UC3)
        val lYCrCb = ArrayList<Mat>(3)

        cvtColor(mat, ycrcb, COLOR_BGR2YCrCb)
        Core.split(mat, lYCrCb)

        return lYCrCb[component.channelID]
    }

    private fun threshold(mat: Mat): Mat {
        val imageThresh = Mat()
        threshold(mat, imageThresh, 0.0, 255.0, THRESH_BINARY + THRESH_OTSU)

        return imageThresh
    }

    fun saveImgToDisk(mat: Mat, postfix: String) {
//        var bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
//        bmp = convertMatToBitMap(mat)
//        releaseImage(listOf(mat))
//
//        var out: FileOutputStream
//        val filename: String = "frame.png"
//        val sd: File = File(getExternalStorageDirectory() + "/frames")
//
//        var success: Boolean = true;
//        if (!sd.exists()) {
//            success = sd.mkdir();
//        }
//        if (success) {
//            val dest: File = File(sd, filename);
//
//            try {
//                out = FileOutputStream(dest);
//                bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
//                // PNG is a lossless format, the compression factor (100) is ignored
//
//            } catch (e: Exception) {
//                Log.d(TAG, e.message);
//            } finally {
//                try {
//                    out.close();
//                    Log.d(TAG, "OK!!");
//                } catch (e: IOException) {
//                    Log.d(TAG, e.message + "Error");
//                }
//            }
//        }
        val timestamp = System.currentTimeMillis()
        Utils.saveImage(NAME_MAIN_FOLDER, "${timestamp}_${postfix}.jpg", convertMatToBitMap(mat)!!, 100)
    }

    fun drawBoundingRects(mat:Mat, contour: List<MatOfPoint>){
        val res = mat
        
    }
}