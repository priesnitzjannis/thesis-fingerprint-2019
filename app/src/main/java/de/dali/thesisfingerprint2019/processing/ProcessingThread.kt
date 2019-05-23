package de.dali.thesisfingerprint2019.processing

import android.graphics.Bitmap
import android.util.Log
import de.dali.thesisfingerprint2019.utils.Utils.releaseImage
import de.dali.thesisfingerprint2019.utils.convertToBitmaps
import org.opencv.core.Mat

class ProcessingThread(private vararg val processingSteps: ProcessingStep) {

    fun process(mat: Mat): List<Bitmap> {
        var image = mat
        val resultImages = mutableListOf<Mat>()

        processingSteps.forEach {
            Log.e(TAG, "Processing step ---->${it.TAG}")

            val startTime = System.currentTimeMillis()

            val result = it.run(image)

            val endTime = System.currentTimeMillis()
            it.addExecutionTimes(endTime - startTime)

            result?.let { procMat ->
                resultImages.add(procMat)
                image = procMat
            }
        }

        val bmps = resultImages.convertToBitmaps()
        releaseImage(resultImages)

        return bmps
    }

    companion object {
        val TAG: String = QualityAssuranceThread::class.java.simpleName
    }

}
