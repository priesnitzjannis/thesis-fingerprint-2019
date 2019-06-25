package de.dali.thesisfingerprint2019.processing

import android.graphics.Bitmap
import android.util.Log
import de.dali.thesisfingerprint2019.processing.Utils.convertMatToBitMap
import org.opencv.core.Mat

class ProcessingThread(vararg val processingSteps: ProcessingStep) {

    fun process(mat: Mat): Bitmap {
        var image = mat

        processingSteps.forEach {
            Log.e(TAG, "Processing step ---->${it.TAG}")

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
        val TAG: String = QualityAssuranceThread::class.java.simpleName
    }

}
