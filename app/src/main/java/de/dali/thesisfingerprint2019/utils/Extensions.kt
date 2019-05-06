package de.dali.thesisfingerprint2019.utils

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

fun List<Mat>.convertToBitmaps(): List<Bitmap> {
    val list = mutableListOf<Bitmap>()

    forEach {
        val bm = Bitmap.createBitmap(it.cols(), it.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(it, bm)

        list.add(bm)
    }

    return list
}

fun Mat.rotate(angle: Double): Mat {
    val radians = Math.toRadians(angle)
    val sin = Math.abs(Math.sin(radians))
    val cos = Math.abs(Math.cos(radians))

    val newWidth = (width() * cos + height() * sin)
    val newHeight = (width() * sin + height() * cos)

    val center = Point(newWidth / 2, newHeight / 2)
    val rotMatrix = Imgproc.getRotationMatrix2D(center, angle, 1.0)

    val size = Size(newWidth, newHeight)

    val result = Mat()
    Imgproc.warpAffine(this, result, rotMatrix, size)

    return result
}