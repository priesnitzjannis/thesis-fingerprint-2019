package de.dali.thesisfingerprint2019.processing

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import de.dali.thesisfingerprint2019.ui.base.custom.ResultView
import de.dali.thesisfingerprint2019.ui.main.handler.UIHandler
import de.dali.thesisfingerprint2019.utils.Utils.releaseImage
import de.dali.thesisfingerprint2019.utils.convertToBitmaps
import de.dali.thesisfingerprint2019.utils.rotate
import org.opencv.core.Mat

class ProcessingPipeline(
    private val uiHandler: UIHandler,
    private vararg val processingSteps: ProcessingStep
) : HandlerThread(TAG, THREAD_PRIORITY_BACKGROUND) {

    var sensorOrientation: Int = 0

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

                var processedMat = Mat()
                val rotatedImage = image.rotate(0.0 - sensorOrientation)

                val resultImages = mutableListOf<Mat>()

                run loop@{
                    processingSteps.forEach {
                        val startTime = System.currentTimeMillis()

                        val result = it.run(rotatedImage, processedMat)

                        val endTime = System.currentTimeMillis()
                        it.addExecutionTimes(endTime - startTime)

                        if (result == null) {
                            return@loop
                        } else {
                            resultImages.add(result)
                            processedMat = result
                        }
                    }
                }

                finalize(resultImages)

                releaseImage(listOf(image, processedMat, rotatedImage))
            }
        }
    }

    fun launch() = start()

    fun close() = quit()

    fun clearQueue() {
        handler.removeCallbacksAndMessages(null)
    }

    fun processImage(originalImage: Mat) {

        val image = originalImage.clone()

        val message = Message()
        message.obj = image

        handler.sendMessage(message)
    }

    fun finalize(mats: List<Mat>) {
        val bmps = mats.convertToBitmaps()
        releaseImage(mats)
        uiHandler.sendMessage(bmps)
    }

    fun setViews(views: Array<out ResultView>) {
        uiHandler.setViews(views)
    }

    companion object {
        val TAG: String = ProcessingPipeline::class.java.simpleName
    }

}
