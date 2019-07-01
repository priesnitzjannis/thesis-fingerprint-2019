package de.dali.thesisfingerprint2019.processing.dali

import de.dali.thesisfingerprint2019.processing.Config.AVG_COLOR_AREA
import de.dali.thesisfingerprint2019.processing.Config.AVG_LINE_IDX
import de.dali.thesisfingerprint2019.processing.Config.GRADIENT_THRESHOLD
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc.*
import javax.inject.Inject


class FindFingerTip @Inject constructor() : ProcessingStep() {
    override val TAG: String
        get() = FindFingerTip::class.java.simpleName

    operator fun Point.plus(p: Point): Point = Point(this.x + p.x, this.y + p.y)

    operator fun Point.div(i: Int): Point = Point(this.x / i, this.y / i)

    private val comparatorDouble = Comparator<Pair<Point, Double>> { p0, p1 ->
        when {
            p0.second > p1.second -> 1
            p0.second == p1.second -> 0
            else -> -1
        }
    }

    private val comparatorY = Comparator<Pair<Point, Double>> { p0, p1 ->
        when {
            p0.first.y > p1.first.y -> 1
            p0.first.y == p1.first.y -> 0
            else -> -1
        }
    }

    override fun run(originalImage: Mat): Mat {
        val tmp = Mat(originalImage.cols(), originalImage.rows(), CvType.CV_8UC1)
        cvtColor(originalImage, tmp, COLOR_BGR2GRAY)

        val outline = calcOutline(tmp)
        val pointPair = createContourPairs(outline)
        val avgLine = calcAvgLine(pointPair)
        val colorGradient = calcColorGradient(avgLine, tmp)
        val normColorGradient = normalizeColorGradient(colorGradient)
        val indexMinima = findMinimaIndex(normColorGradient)
        return cropToFingerTip(indexMinima, outline, originalImage)
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

    private fun calcOutline(originalImage: Mat): List<Point> {
        val thresh = Mat(originalImage.cols(), originalImage.rows(), CvType.CV_8UC1)
        threshold(originalImage, thresh, 1.0, 255.0, THRESH_BINARY)

        val contours = mutableListOf<Point>()

        for (i in 0 until thresh.rows()) {

            val allPointsInRow = mutableListOf<Point>()

            for (j in 0 until thresh.cols()) {
                val colour = thresh.get(i, j)

                if (colour[0] != 0.0) {
                    allPointsInRow.add(Point(j.toDouble(), i.toDouble()))
                }
            }

            if (allPointsInRow.size >= 2) {
                contours.add(allPointsInRow.first())
                contours.add(allPointsInRow.last())
            }
        }

        return contours
    }

    private fun createContourPairs(contour: List<Point>): List<Pair<Point, Point>> {
        val contourPairs = mutableListOf<Pair<Point, Point>>()

        for (i in 0 until contour.size step 2) {
            contourPairs.add(Pair(contour[i], contour[i + 1]))
        }

        return contourPairs
    }

    private fun calcAvgLine(contourPairs: List<Pair<Point, Point>>): List<Point> {
        val avgLine = mutableListOf<Point>()

        for (contourPair in contourPairs) {
            val avg = (contourPair.first + contourPair.second) / 2
            avgLine.add(avg)
        }

        return avgLine
    }

    private fun calcColorGradient(avgLine: List<Point>, originalImage: Mat): List<Pair<Point, Double>> {
        val colorGradient = mutableListOf<Pair<Point, Double>>()
        val colorGradientRow = mutableListOf<Double>()

        val temp = Mat(originalImage.cols(), originalImage.rows(), CvType.CV_8UC1)
        GaussianBlur(originalImage, temp, Size(5.0, 5.0), 0.0)

        for (j in AVG_LINE_IDX until (avgLine.size - AVG_LINE_IDX)) {
            for (i in -AVG_COLOR_AREA..AVG_COLOR_AREA) {
                val colAtPos = temp.get(avgLine[j].y.toInt(), avgLine[j].x.toInt() + i)[0]
                colorGradientRow.add(colAtPos)
            }

            val meanColor = colorGradientRow.sum() / colorGradientRow.size
            colorGradient.add(Pair(avgLine[j], meanColor))
            colorGradientRow.clear()
        }

        return colorGradient
    }

    private fun normalizeColorGradient(colorGradient: List<Pair<Point, Double>>): List<Pair<Point, Double>> {
        val min = colorGradient.minWith(comparatorDouble)
        val max = colorGradient.maxWith(comparatorDouble)

        return colorGradient.map {
            Pair(it.first, normalizeValue(it.second, min!!.second, max!!.second))
        }
    }

    private fun findMinimaIndex(colorGradient: List<Pair<Point, Double>>): List<Pair<Point, Double>> =
        colorGradient.filter {
            it.second < GRADIENT_THRESHOLD
        }

    private fun normalizeValue(value: Double, minValue: Double, maxValue: Double): Double {
        return (2.0 * ((value - minValue) / (maxValue - minValue)) - 1.0)
    }

    private fun cropToFingerTip(indexMinima: List<Pair<Point, Double>>, contour: List<Point>, originalImage: Mat): Mat {
        val croppedContour = mutableListOf<Point>()
        val minimaRight: Point

        minimaRight = if (indexMinima.isEmpty()) {
            Point(originalImage.cols() * (2.0 / 3.0), 0.0)
        } else {
            val maxValueColorGradient = indexMinima.minWith(comparatorY)
            maxValueColorGradient!!.first
        }

        contour.forEach {
            if (minimaRight.y >= it.y) {
                croppedContour.add(it)
            }
        }

        val boundingRect = boundingRect(MatOfPoint2f(*croppedContour.toTypedArray()))

        val image = Mat(originalImage, boundingRect)

        val bmpResult = Utils.convertMatToBitMap(image)

        return image
    }

}