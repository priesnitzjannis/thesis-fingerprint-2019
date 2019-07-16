package de.dali.thesisfingerprint2019.processing

import android.graphics.Bitmap
import android.util.Log
import de.dali.thesisfingerprint2019.processing.Utils.convertMatToBitMap
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class ProcessingThread(vararg val processingSteps: ProcessingStep) {

    lateinit var grayMat: Mat

    fun process(mat: Mat): Bitmap {
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY)

        var image = grayMat

        processingSteps.forEach {
            Log.e(TAG, it.TAG)
            val startTime = System.currentTimeMillis()

            val result = it.run(image)

            val endTime = System.currentTimeMillis()
            it.addExecutionTimes(endTime - startTime)

            result.let { procMat ->
                image = procMat
            }
        }
        return convertMatToBitMap(image)!!
    }

    companion object {
        val TAG: String = ProcessingThread::class.java.simpleName
    }

}
