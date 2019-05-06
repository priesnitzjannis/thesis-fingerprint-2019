package de.dali.thesisfingerprint2019.utils

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import org.opencv.core.Mat

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

}