package de.dali.thesisfingerprint2019.processing

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintIntermediateEntity
import de.dali.thesisfingerprint2019.processing.Utils.HAND
import de.dali.thesisfingerprint2019.processing.Utils.HAND.NOT_SPECIFIED
import de.dali.thesisfingerprint2019.processing.Utils.releaseImage
import de.dali.thesisfingerprint2019.processing.Utils.rotateImageByDegree
import de.dali.thesisfingerprint2019.processing.common.RotateFinger
import de.dali.thesisfingerprint2019.processing.dali.*
import org.opencv.core.Mat


class QualityAssuranceThread(vararg val processingStep: ProcessingStep) :
    HandlerThread(TAG, THREAD_PRIORITY_BACKGROUND) {

    var sensorOrientation: Int = 0
    var totalImages: Int = 0
    var processedImages: Int = 0
    var highestEdgeDenseMats = mutableListOf<FingerPrintIntermediateEntity>()

    var amountOfFinger: Int = 0
        set(value) {
            field = value
            (processingStep[2] as FingerBorderDetection).amountOfFinger = value
        }

    var hand: HAND = NOT_SPECIFIED
        set(value) {
            field = value
            (processingStep[1] as FingerRotationImprecise).hand = value
            (processingStep[3] as RotateFinger).hand = value
        }

    lateinit var onSuccess: (List<FingerPrintIntermediateEntity>) -> Unit
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
                    val degreeImprecise = (processingStep[1] as FingerRotationImprecise).correctionAngle

                    val separatedFingers = (processingStep[2] as FingerBorderDetection).runReturnMultiple(rotatedFinger)

                    if (separatedFingers.isNotEmpty()) {

                        val rotatedFingers = separatedFingers.map { (processingStep[3] as RotateFinger).run(it) }
                        val fingertips = rotatedFingers.map { (processingStep[4] as FindFingerTip).run(it) }

                        val qualityCheckedImages = mutableListOf<FingerPrintIntermediateEntity>()

                        fingertips.forEach {
                            val qualityCheckedImage = (processingStep[5] as MultiQualityAssurance).run(it)
                            val edgeDens = (processingStep[5] as MultiQualityAssurance).edgeDensity

                            val fingerPrintIntermediate =
                                FingerPrintIntermediateEntity(qualityCheckedImage, edgeDens, degreeImprecise)
                            Log.e(TAG, "Edge Dens : $edgeDens")

                            qualityCheckedImages.add(fingerPrintIntermediate)
                        }

                        if (highestEdgeDenseMats.isEmpty()) {
                            highestEdgeDenseMats.addAll(qualityCheckedImages)
                        } else {
                            highestEdgeDenseMats.mapInPlace(qualityCheckedImages)
                        }

                        Log.e(TAG, "Processed Image")

                        processedImages++

                        releaseImage(listOf(image, processedMat, rotatedImage))
                        if (processedImages == 5) {
                            clearQueue()
                            quit()
                            onSuccess(highestEdgeDenseMats)
                        }
                    } else {
                        releaseImage(listOf(image, processedMat, rotatedImage))
                        Log.e(TAG, "FingerBorderDetection couldn't split Fingers")
                        onFailure("FingerBorderDetection couldn't split Fingers")
                    }
                } else {
                    releaseImage(listOf(image, processedMat, rotatedImage))
                    Log.e(TAG, "MultiFingerDetection couldn't detect Fingers")
                    onFailure("MultiFingerDetection couldn't detect Fingers")
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

    private fun MutableList<FingerPrintIntermediateEntity>.mapInPlace(l: List<FingerPrintIntermediateEntity>) {
        val iterateA = this.listIterator()
        val iterateB = l.listIterator()

        while (iterateA.hasNext() && iterateB.hasNext()) {
            val valA = iterateA.next()
            val valB = iterateB.next()
            if (valA.edgeDens < valB.edgeDens) {
                iterateA.set(valB)
            }
        }
    }

    fun setSuccessCallback(callback: (List<FingerPrintIntermediateEntity>) -> Unit) {
        this.onSuccess = callback
    }

    fun setFailureCallback(callback: (String) -> Unit) {
        this.onFailure = callback
    }


    companion object {
        val TAG: String = QualityAssuranceThread::class.java.simpleName
    }

}
