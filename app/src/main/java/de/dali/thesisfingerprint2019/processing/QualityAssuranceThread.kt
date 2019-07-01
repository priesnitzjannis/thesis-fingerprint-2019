package de.dali.thesisfingerprint2019.processing

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import de.dali.thesisfingerprint2019.processing.Utils.releaseImage
import de.dali.thesisfingerprint2019.processing.Utils.rotateImageByDegree
import de.dali.thesisfingerprint2019.processing.dali.FingerBorderDetection
import de.dali.thesisfingerprint2019.processing.dali.FingerRotationImprecise
import de.dali.thesisfingerprint2019.processing.dali.MultiFingerDetection
import de.dali.thesisfingerprint2019.processing.dali.MultiQualityAssurance
import org.opencv.core.Mat


class QualityAssuranceThread(private vararg val processingStep: ProcessingStep) :
    HandlerThread(TAG, THREAD_PRIORITY_BACKGROUND) {

    var sensorOrientation: Int = 0
    var totalImages: Int = 0
    var processedImages: Int = 0
    var highestEdgeDenseMats = mutableListOf<Pair<Mat, Double>>()

    var amountOfFinger: Int = 0
        set(value) {
            field = value
            (processingStep[2] as FingerBorderDetection).amountOfFinger = value
        }

    lateinit var onSuccess: (List<Pair<Mat, Double>>) -> Unit
    lateinit var onFailure: (String) -> Unit

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        handler = getHandler(looper)
    }

    private lateinit var handler: Handler

    private fun getHandler(looper: Looper): Handler {
        return object : Handler(looper) {

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                val image = (msg.obj as Mat)

                totalImages++

                val processedMat = Mat()
                val rotatedImage = rotateImageByDegree(0.0 - sensorOrientation, image)

                val multiFingerImage = (processingStep[0] as MultiFingerDetection).run(rotatedImage)

                if (!multiFingerImage.empty()) {
                    val rotatedFinger = (processingStep[1] as FingerRotationImprecise).run(multiFingerImage)

                    val separatedFingers =
                        (processingStep[2] as FingerBorderDetection).runReturnMultiple(rotatedFinger)

                    if (separatedFingers.isNotEmpty()) {
                        val qualityCheckedImages = mutableListOf<Pair<Mat, Double>>()

                        separatedFingers.forEach {
                            val qualityCheckedImage = (processingStep[3] as MultiQualityAssurance).run(it)
                            val edgeDens = (processingStep[3] as MultiQualityAssurance).edgeDensity

                            Log.e(TAG, "Edge Dens : $edgeDens")

                            qualityCheckedImages.add(Pair(qualityCheckedImage, edgeDens))
                        }

                        if (highestEdgeDenseMats.isEmpty()) {
                            highestEdgeDenseMats.addAll(qualityCheckedImages)
                        } else {
                            highestEdgeDenseMats.mapInPlace(qualityCheckedImages)
                        }

                        Log.e(TAG, "Processed Image")

                        processedImages++

                        releaseImage(listOf(image, processedMat, rotatedImage))
                        if (processedImages == 5) onSuccess(highestEdgeDenseMats)

                    } else {
                        releaseImage(listOf(image, processedMat, rotatedImage))
                        Log.e(TAG, "FingerBorderDetection couldn't split Fingers")
                        onFailure("")
                    }
                } else {
                    releaseImage(listOf(image, processedMat, rotatedImage))
                    Log.e(TAG, "MultiFingerDetection couldn't detect Fingers")
                    onFailure("")
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

    private fun MutableList<Pair<Mat, Double>>.mapInPlace(l: List<Pair<Mat, Double>>) {
        val iterateA = this.listIterator()
        val iterateB = l.listIterator()

        while (iterateA.hasNext() && iterateB.hasNext()) {
            val valA = iterateA.next()
            val valB = iterateB.next()
            if (valA.second < valB.second) {
                iterateA.set(valB)
            }
        }
    }

    fun setSuccessCallback(callback: (List<Pair<Mat, Double>>) -> Unit) {
        this.onSuccess = callback
    }

    fun setFailureCallback(callback: (String) -> Unit) {
        this.onFailure = callback
    }


    companion object {
        val TAG: String = QualityAssuranceThread::class.java.simpleName
    }

}
