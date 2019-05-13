package de.dali.thesisfingerprint2019.utils

import android.content.Context
import android.graphics.Bitmap
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Environment
import org.opencv.core.Mat
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

    fun toReadableDate(time: Long): Date = Date(time)

    private fun saveImage(finalBitmap: Bitmap, quality: Int) {
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/saved_images")
        myDir.mkdirs()

        val name = "${System.currentTimeMillis()}.jpg"

        val file = File(myDir, name)
        if (file.exists()) file.delete()
        val out = FileOutputStream(file)
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        out.flush()
        out.close()
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

}