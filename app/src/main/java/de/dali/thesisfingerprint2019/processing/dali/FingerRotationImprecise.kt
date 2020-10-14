package de.dali.thesisfingerprint2019.processing.dali

import android.util.Log
import de.dali.thesisfingerprint2019.logging.Logging
import de.dali.thesisfingerprint2019.processing.Config
import de.dali.thesisfingerprint2019.processing.ProcessingStep
import de.dali.thesisfingerprint2019.processing.Utils.HAND
import de.dali.thesisfingerprint2019.processing.Utils.HAND.NOT_SPECIFIED
import de.dali.thesisfingerprint2019.processing.Utils.getThresholdImageNew
import de.dali.thesisfingerprint2019.processing.Utils.rotateImageByDegree
import org.opencv.core.Mat
import javax.inject.Inject


class FingerRotationImprecise @Inject constructor() : ProcessingStep() {
    var correctionAngle = 0.0

    var hand: HAND = NOT_SPECIFIED

    override val TAG: String
        get() = FingerRotationImprecise::class.java.simpleName

    override fun run(originalImage: Mat): Mat {
        //val rotatedImage = originalImage
        Logging.createLogEntry(
            Logging.loggingLevel_param,
            1700,
            "Config data for Finger Rotation Imprecise:\nPOINT_PAIR_DST = " + Config.POINT_PAIR_DST
        )
        val start = System.currentTimeMillis()

        var angle = 0.0

        // -- NEW VERSION --
        try {
            val thresh = getThresholdImageNew(originalImage)
            val height = thresh.rows()
            val width = thresh.cols()
            val lBorderPixel = mutableListOf<Int>()


            for (i in width / 2 until width - 1){
                val foo = thresh.get(height - 1, i)
                lBorderPixel.add(foo[0].toInt())
            }
            for (i in 0 until height){
                val foo = thresh.get(height - 1 - i, width - 1)
                lBorderPixel.add(foo[0].toInt())
            }
            for (i in 0 until width){
                val foo = thresh.get(0, width - i - 1)
                lBorderPixel.add(foo[0].toInt())
            }
            for (i in 0 until height){
                val foo = thresh.get(i, 0)
                lBorderPixel.add(foo[0].toInt())
            }
            for (i in 0 until width / 2){
                val foo = thresh.get(height - 1, i)
                lBorderPixel.add(foo[0].toInt())
            }

            val reducedToDeg = (lBorderPixel.size / 360) + 1

            val lreducedToDeg = mutableListOf<Int>()

            var cnt = 0

            for (i in 0 until lBorderPixel.size){
                if (lBorderPixel.get(i) == 255){
                    cnt = cnt + 1
                }
                if (i % reducedToDeg == reducedToDeg - 1){
                    lreducedToDeg.add(cnt)
                    cnt = 0
                }
            }

            var x = 0.0
            var y = 0.0
            for (i in 0 until lreducedToDeg.size)
                if (lreducedToDeg.get(i) > reducedToDeg / 2) {
                    x = x + Math.cos(Math.toRadians(i.toDouble()))
                    y = y + Math.sin(Math.toRadians(i.toDouble()))
                }

            angle = Math.toDegrees(Math.atan2(y, x))
            angle = -angle

        } catch (e:Exception){
            return originalImage
        }

        Log.e("Rotation angle", "Angle: " + angle.toString())

        val rotatedImage = rotateImageByDegree((angle).toDouble(), originalImage)

        val duration = System.currentTimeMillis() - start
        Logging.createLogEntry(
            Logging.loggingLevel_medium,
            1700,
            "Finger Rotation Imprecise finished in " + duration + "ms."
        )


        Logging.createLogEntry(
            Logging.loggingLevel_critical,
            1700,
            "Finger oriented, rotated by " + angle + "°, see images for results.",
            rotatedImage
        )

        return rotatedImage
    }

    override fun runReturnMultiple(originalImage: Mat): List<Mat> {
        throw NotImplementedError("Not implemented for this processing step.")
    }

}