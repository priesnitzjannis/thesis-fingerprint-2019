package de.dali.thesisfingerprint2019.utils

import android.content.Context
import android.graphics.Bitmap
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Environment
import android.util.Log
import de.dali.thesisfingerprint2019.utils.Constants.NAME_MAIN_FOLDER
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


object Utils {

    val TAG = Utils::class.java.simpleName

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
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return sensorOrientation
    }

    fun toReadableDate(time: Long): Date = Date(time)

    ///storage/sdcard0/thesis-fingerprints-2019/Logging-Images
    fun saveImage(pathName: String, fileName: String, bitmap: Bitmap, quality: Int) {
        val pathname = "${Environment.getExternalStorageDirectory()}/$pathName"

        val myDir = File(pathname)
        try {
            myDir.mkdirs()
        }  catch (e: Exception) {
            Log.e(TAG, e.message)
        }

        if (!myDir.exists()) myDir.mkdirs()

        val file = File(myDir, fileName)
        if (file.exists()) file.delete()
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        out.flush()
        out.close()
    }

    fun exportDB(context: Context) {
        try {
            val sd = Environment.getExternalStorageDirectory()

            if (sd.canWrite()) {
                val currentDB = context.getDatabasePath("fingerprint-database.db")
                val backupDBPath = "$NAME_MAIN_FOLDER/fingerprint-database.db"
                val backupDB = File(sd, backupDBPath)

                if (currentDB.exists()) {
                    val src = FileInputStream(currentDB).channel
                    val dst = FileOutputStream(backupDB).channel
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

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