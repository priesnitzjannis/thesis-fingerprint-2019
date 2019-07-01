package de.dali.thesisfingerprint2019.processing

import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc
import org.opencv.utils.Converters

fun List<MatOfPoint>.toMat(): Mat {
    val list = mutableListOf<Point>()

    this.forEach {
        list.addAll(it.toList())
    }

    return Converters.vector_Point_to_Mat(list)
}

fun Mat.cropToMinArea(): Mat {
    val thresh = Utils.getThresholdImage(this)
    val contour = Utils.getContour(thresh)
    val rect = Imgproc.boundingRect(contour.toMat())

    return Mat(this, rect)
}

operator fun Point.minus(p: Point) = Point(this.x - p.x, this.y - p.y)
