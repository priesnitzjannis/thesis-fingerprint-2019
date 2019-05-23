package de.dali.thesisfingerprint2019.processing

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import de.dali.thesisfingerprint2019.processing.stein.QualityAssurance
import de.dali.thesisfingerprint2019.utils.Utils.releaseImage
import de.dali.thesisfingerprint2019.utils.rotate
import org.opencv.core.Mat


class QualityAssuranceThread(private val qualityAssurance: QualityAssurance) :
    HandlerThread(TAG, THREAD_PRIORITY_BACKGROUND) {

    var sensorOrientation: Int = 0

    var highestEdgeDensity: Double = 0.0
    lateinit var highestEdgeDenseMat: Mat

    var imageCounter: Int = 0

    private lateinit var callback: (Mat) -> Unit

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        handler = getHandler(looper)
    }

    private lateinit var handler: Handler

    private fun getHandler(looper: Looper): Handler {
        return object : Handler(looper) {

            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                val image = (msg?.obj as Mat)
                val processedMat = Mat()
                val rotatedImage = image.rotate(0.0 - sensorOrientation)

                val result = qualityAssurance.run(rotatedImage)

                if (result != null) {
                    imageCounter++
                    if (qualityAssurance.edgeDensity > highestEdgeDensity) {
                        highestEdgeDensity = qualityAssurance.edgeDensity
                        highestEdgeDenseMat = result.clone()
                    } else {
                        releaseImage(listOf(result))
                    }
                }

                releaseImage(listOf(image, processedMat, rotatedImage))

                if (imageCounter == 10) {
                    callback(highestEdgeDenseMat)
                }

            }
        }
    }

    fun launch() = start()

    fun close() = quit()

    fun clearQueue() = handler.removeCallbacksAndMessages(null)

    fun sendToPipeline(originalImage: Mat) {
        val message = Message()
        val image = originalImage.clone()
        message.obj = image
        handler.sendMessage(message)
    }

    fun setCallback(callback: (Mat) -> Unit) {
        this.callback = callback
    }

    companion object {
        val TAG: String = QualityAssuranceThread::class.java.simpleName
    }

}
