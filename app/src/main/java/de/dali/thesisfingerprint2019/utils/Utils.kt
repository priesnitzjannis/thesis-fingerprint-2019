package de.dali.thesisfingerprint2019.utils

import android.content.Context
import android.graphics.Bitmap
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Environment
import android.util.Log
import de.dali.thesisfingerprint2019.utils.Constants.NAME_MAIN_FOLDER
import java.util.*
import java.io.*


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

    fun saveImage(pathName: String, fileName: String, bitmap: Bitmap, quality: Int) {
        val pathname = "${Environment.getExternalStorageDirectory()}/$pathName"
        val myDir = File(pathname)

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

    fun copyFile(input: File, output: File, outputPath: String) {

        val inputStream: InputStream?
        val out: OutputStream?
        try {
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            inputStream = FileInputStream(input)
            out = FileOutputStream(output)

            val buffer = ByteArray(1024)
            var read: Int
            while ((inputStream.read(buffer)).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            inputStream.close()

            out.flush()
            out.close()

        } catch (fnfe1: FileNotFoundException) {
            Log.e("tag", fnfe1.message)
        } catch (e: Exception) {
            Log.e("tag", e.message)
        }

    }
    fun copyAttachedDatabase(context: Context, databaseName: String) {
        val dbPath = context.getDatabasePath(databaseName)

        // If the database already exists, return
        if (dbPath.exists()) {
            return
        }

        // Make sure we have a path to the file
        dbPath.parentFile.mkdirs()

        // Try to copy database file
        try {
            val inputStream = FileInputStream(Environment.getExternalStorageDirectory().absolutePath + "/$NAME_MAIN_FOLDER/fingerprint-database.db")
            val output = FileOutputStream(dbPath)

            val buffer = ByteArray(8192)

            var length : Int
            while (inputStream.read(buffer, 0, 8192).also { length = it } > 0) {
                output.write(buffer, 0, length)
            }

            output.flush()
            output.close()
            inputStream.close()
        } catch (e: IOException) {
            Log.d(TAG, "Failed to open file", e)
            e.printStackTrace()
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