package de.dali.thesisfingerprint2019.utils

import android.content.Context
import android.graphics.Bitmap
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Environment
import android.util.Log
import org.opencv.core.CvException
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import java.util.*

object Utils {

    fun getSensorOrientation(context: Context): Int {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        var sensorOrientation = 0
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)
                val orientation = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (orientation == CameraCharacteristics.LENS_FACING_BACK) {
                    sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        return sensorOrientation
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

    fun toReadableDate(time: Long): Date = Date(time)

    fun saveImages(
        folderMain: String,
        folderUser: String,
        folderFingerprint: String,
        finalBitmaps: List<Bitmap>,
        quality: Int
    ): List<String> {
        val pathname = "${Environment.getExternalStorageDirectory()}/$folderMain/$folderUser/$folderFingerprint"
        val myDir = File(pathname)

        val listOfImages = mutableListOf<String>()

        if (!myDir.exists()) myDir.mkdirs()

        finalBitmaps.forEach {
            val name = "${System.currentTimeMillis()}.jpg"

            val file = File(myDir, name)
            if (file.exists()) file.delete()
            val out = FileOutputStream(file)
            it.compress(Bitmap.CompressFormat.JPEG, quality, out)
            out.flush()
            out.close()

            listOfImages.add("$folderUser/$folderFingerprint/$name")
        }

        return listOfImages
    }

    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }


    private fun capitalize(s: String?): String {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first) + s.substring(1)
        }
    }

    fun convertMatToBitMap(input: Mat): Bitmap? {
        var bmp: Bitmap? = null
        val rgb = Mat()

        Imgproc.cvtColor(input,rgb, Imgproc.COLOR_RGB2RGBA,4)

        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888)

            org.opencv.android.Utils.matToBitmap(input, bmp, true)
        } catch (e: CvException) {
            Log.d("Exception", e.message)
        }

        return bmp
    }

}