package de.dali.demonstrator.processing.common


import de.dali.demonstrator.logging.Logging
import de.dali.demonstrator.processing.ProcessingStep
import de.dali.demonstrator.processing.Utils.HAND.NOT_SPECIFIED
import de.dali.demonstrator.processing.Utils.getFingerContour
import de.dali.demonstrator.processing.Utils.getThresholdImageNew
import de.dali.demonstrator.processing.Utils.rotateImageByDegree
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc
import javax.inject.Inject

class RotateFinger @Inject constructor() : ProcessingStep() {

    var degreeImprecise = 0.0

    var correctionAngle = 0.0

    var hand = NOT_SPECIFIED

    override val TAG: String
        get() = RotateFinger::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        val start = System.currentTimeMillis()
        var rotatedFinger = originalImage


        // -- NEW VERSION --
        val thresh = getThresholdImageNew(originalImage)
        var angle = 0.0
        val matrix: MutableList<MatOfPoint2f> = ArrayList()
        val contours = getFingerContour(thresh)
        val myPt = MatOfPoint2f()
        try {
            contours[0].convertTo(myPt, CvType.CV_32FC2)

            val rect = Imgproc.minAreaRect(myPt)
            angle = rect.angle
            if (angle > 45.0 && angle < 135.0) {
                angle = angle - 90
            } else if (angle < -45.0 && angle < 135.0){
                angle = angle + 90
            } else if(angle > 135.0 && angle < 135.0){
                angle = angle + 180

            }
            rotatedFinger = rotateImageByDegree(angle, originalImage)
        } catch (e:IndexOutOfBoundsException){
            rotatedFinger = originalImage

        }



        val duration = System.currentTimeMillis() - start
        Logging.createLogEntry(Logging.loggingLevel_medium, 1800, "Finger Rotation finished in " + duration + "ms.")

        // could add rotation degree to the image, represented by line
        Logging.createLogEntry(Logging.loggingLevel_critical, 1800, "Finger rotated by " + angle + "°, see image for results.", rotatedFinger)

        return rotatedFinger
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    private fun generatePointPair(middle: Point, i: Int): Pair<Point, Point> =
        Pair(Point(middle.x, middle.y - i), Point(middle.x, middle.y + i))

    private fun calcCenterPointOfMat(resultFrame: Mat): Point =
        Point(resultFrame.cols() / 2.0, resultFrame.rows() / 2.0)

}