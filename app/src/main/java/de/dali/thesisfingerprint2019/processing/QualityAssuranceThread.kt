package de.dali.thesisfingerprint2019.processing

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintIntermediateEntity
import de.dali.thesisfingerprint2019.processing.QualityAssuranceThread.IntermediateResults.FAILURE
import de.dali.thesisfingerprint2019.processing.QualityAssuranceThread.IntermediateResults.SUCCESSFUL
import de.dali.thesisfingerprint2019.processing.Utils.HAND
import de.dali.thesisfingerprint2019.processing.Utils.HAND.NOT_SPECIFIED
import de.dali.thesisfingerprint2019.processing.Utils.hasEnoughContent
import de.dali.thesisfingerprint2019.processing.Utils.hasValidSize
import de.dali.thesisfingerprint2019.processing.Utils.releaseImage
import de.dali.thesisfingerprint2019.processing.Utils.rotateImageByDegree
import de.dali.thesisfingerprint2019.processing.common.RotateFinger
import de.dali.thesisfingerprint2019.processing.dali.*
import org.opencv.core.Mat
import kotlin.system.measureTimeMillis


class QualityAssuranceThread(vararg val processingStep: ProcessingStep) :
    HandlerThread(TAG, THREAD_PRIORITY_BACKGROUND) {

    enum class IntermediateResults {
        SUCCESSFUL,
        FAILURE
    }

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
    lateinit var onUpdate: (IntermediateResults, String, Int) -> Unit

    var imageProcessingRunning = false


    inline fun <T> measureTimeMillis(loggingFunction: (Long) -> Unit,
                                     function: () -> T): T {

        val startTime = System.currentTimeMillis()
        val result: T = function.invoke()
        loggingFunction.invoke(System.currentTimeMillis() - startTime)

        return result
    }

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

                val rotatedImage =
                    measureTimeMillis({ time -> Log.d(TAG, "Sensor Rotate:  $time") }) {
                        rotateImageByDegree(0.0 - sensorOrientation, image)
                    }

                val multiFingerImage = measureTimeMillis({ time -> Log.d(TAG, "Detect Finger:  $time") }) {
                    (processingStep[0] as MultiFingerDetection).run(rotatedImage)
                }

                if (!multiFingerImage.empty()) {
                    val rotatedFinger = measureTimeMillis({ time -> Log.d(TAG, "FingerRotationImprecise:  $time") }) {
                        (processingStep[1] as FingerRotationImprecise).run(multiFingerImage)
                    }
                    val degreeImprecise = measureTimeMillis({ time -> Log.d(TAG, "FingerRotationImprecise 2:  $time") }) {
                        (processingStep[1] as FingerRotationImprecise).correctionAngle
                    }
                    val separatedFingers = measureTimeMillis({ time -> Log.d(TAG, "FingerBorderDetection:  $time") }) {
                        (processingStep[2] as FingerBorderDetection).runReturnMultiple(rotatedFinger)
                    }
                    if (separatedFingers.isNotEmpty()) {

                        val rotatedFingers = separatedFingers.map {
                            (processingStep[3] as RotateFinger).degreeImprecise = degreeImprecise
                            (processingStep[3] as RotateFinger).run(it)
                        }

                        val fingertips =
                            rotatedFingers.map { (processingStep[4] as FindFingerTip).run(it) }

                        val qualityCheckedImages = mutableListOf<FingerPrintIntermediateEntity>()

                        fingertips.forEach {
                            val qualityCheckedImage =
                                (processingStep[5] as MultiQualityAssurance).run(it)
                            val edgeDens = (processingStep[5] as MultiQualityAssurance).edgeDensity

                            val fingerPrintIntermediate =
                                FingerPrintIntermediateEntity(
                                    qualityCheckedImage,
                                    edgeDens,
                                    degreeImprecise
                                )

                            qualityCheckedImages.add(fingerPrintIntermediate)
                        }
                        if (qualityCheckedImages.none { it.edgeDens < 100.0 }) {//5.0
                            if (highestEdgeDenseMats.isEmpty()) {
                                highestEdgeDenseMats.addAll(qualityCheckedImages)
                            } else {
                                highestEdgeDenseMats.mapInPlace(qualityCheckedImages)
                            }

                            processedImages++

                            releaseImage(listOf(image, processedMat, rotatedImage))

                            onUpdate(SUCCESSFUL, "Processed frame successfully.", processedImages)

                            if (processedImages == 1) {
                                clearQueue()
                                quit()
                                onSuccess(highestEdgeDenseMats)
                            }
                        } else {
                            onUpdate(FAILURE, "Fingers too blurry.", processedImages)
                        }
                    } else {
                        releaseImage(listOf(image, processedMat, rotatedImage))
                        onUpdate(FAILURE, "Couldn't split Fingers.", processedImages)
                    }
                } else {
                    releaseImage(listOf(image, processedMat, rotatedImage))
                    onUpdate(FAILURE, "#01 Couldn't detect Fingers.", processedImages)
                }

                imageProcessingRunning = false
            }
        }
    }

    fun launch() = start()

    fun close() = quit()

    fun clearQueue() = handler.removeCallbacksAndMessages(null)

    fun sendToPipeline(originalImage: Mat) {
        val message = Message()

        if (!imageProcessingRunning) {
            imageProcessingRunning = true
            val image = originalImage.clone()
            message.obj = image
            handler.sendMessage(message)
        }
    }

    private fun MutableList<FingerPrintIntermediateEntity>.mapInPlace(l: List<FingerPrintIntermediateEntity>) {
        val iterateA = this.listIterator()
        val iterateB = l.listIterator()

        while (iterateA.hasNext() && iterateB.hasNext()) {
            val valA = iterateA.next()
            val valB = iterateB.next()

            if (!hasValidSize(valA.mat) && !hasEnoughContent(valA.mat) && hasValidSize(valB.mat) && hasEnoughContent(valB.mat)) {
                iterateA.set(valB)
            } else if (valA.edgeDens < valB.edgeDens && hasValidSize(valB.mat) && hasEnoughContent(valB.mat)) {
                iterateA.set(valB)
            }
        }
    }

    fun setSuccessCallback(callback: (List<FingerPrintIntermediateEntity>) -> Unit) {
        this.onSuccess = callback
    }

    fun setUpdateCallback(callback: (IntermediateResults, String, Int) -> Unit) {
        this.onUpdate = callback
    }


    companion object {
        val TAG: String = QualityAssuranceThread::class.java.simpleName
    }

}
