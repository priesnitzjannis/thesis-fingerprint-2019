package de.dali.thesisfingerprint2019.processing

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintIntermediateEntity
import de.dali.thesisfingerprint2019.logging.Logging
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

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        handler = getHandler(looper)
    }

    private lateinit var handler: Handler

    private fun getHandler(looper: Looper): Handler {
        return object : Handler(looper) {

            override fun handleMessage(msg: Message) {
                val start = System.currentTimeMillis()

                super.handleMessage(msg)
                val image = (msg.obj as Mat)
                //val image = Utils.readImageFromDisk()
                totalImages++

                try {
                    Logging.startRun()
                    Logging.createLogEntry(
                        Logging.loggingLevel_critical,
                        1100,
                        "Started processing of an image.",
                        image
                    )
                } catch (e: NullPointerException){
                    e.printStackTrace()
                }


                val processedMat = Mat()
                //val rotatedImage = rotateImageByDegree(0.0 - sensorOrientation, image)

                val multiFingerImage = (processingStep[0] as MultiFingerDetection).run(image)

                if (!multiFingerImage.empty()) {
                    val rotatedFinger =
                        (processingStep[1] as FingerRotationImprecise).run(multiFingerImage)
                    val degreeImprecise =
                        (processingStep[1] as FingerRotationImprecise).correctionAngle

                    val separatedFingers =
                        (processingStep[2] as FingerBorderDetection).runReturnMultiple(rotatedFinger)

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
                            val imgSize = (processingStep[5] as MultiQualityAssurance).validImageSize
                            val fingerPrintIntermediate =
                                FingerPrintIntermediateEntity(
                                    qualityCheckedImage,
                                    edgeDens,
                                    imgSize,
                                    degreeImprecise
                                )

                            qualityCheckedImages.add(fingerPrintIntermediate)
                        }
                        if (qualityCheckedImages.none { it.edgeDens < 5.0 }) {

                            if (qualityCheckedImages.all { it.imgSizeOk == true }) {
                                if (highestEdgeDenseMats.isEmpty()) {
                                    highestEdgeDenseMats.addAll(qualityCheckedImages)
                                } else {
                                    highestEdgeDenseMats.mapInPlace(qualityCheckedImages)
                                }

                                processedImages++

                                releaseImage(listOf(image, processedMat)) //rotatedImage

                                onUpdate(SUCCESSFUL, "Processed frame successfully.", processedImages)

                                Logging.createLogEntry(
                                    Logging.loggingLevel_critical,
                                    1100,
                                    "Processing of image completed."
                                )
                                Logging.endRun(0)

                                if (processedImages == 5) {
                                    clearQueue()
                                    quit()
                                    onSuccess(highestEdgeDenseMats)
                                    Logging.createLogEntry(Logging.loggingLevel_critical, 1100, "Processing completed.")
                                }
                            } else {
                                onUpdate(FAILURE, "Finger Segmentation failed", processedImages)
                                Logging.createLogEntry(
                                    Logging.loggingLevel_critical,
                                    1100,
                                    "Processing cancelled. Image size inappropriate."
                                )
                                Logging.endRun(-1)
                            }

                        } else {
                            onUpdate(FAILURE, "Fingers too blurry.", processedImages)
                            Logging.createLogEntry(
                                Logging.loggingLevel_critical,
                                1100,
                                "Processing cancelled. Fingers too blurry."
                            )
                            Logging.endRun(-1)
                        }
                    } else {
                        releaseImage(listOf(image, processedMat)) //rotatedImage
                        onUpdate(FAILURE, "Couldn't split Fingers.", processedImages)
                        Logging.createLogEntry(
                            Logging.loggingLevel_critical,
                            1100,
                            "Processing cancelled. Couldn't split Fingers."
                        )
                        Logging.endRun(-2)
                    }
                } else {
                    releaseImage(listOf(image, processedMat)) //rotatedImage
                    onUpdate(FAILURE, "Couldn't detect Fingers.", processedImages)
                    Logging.createLogEntry(
                        Logging.loggingLevel_critical,
                        1100,
                        "Processing cancelled. Couldn't detect Fingers."
                    )
                    Logging.endRun(-3)
                }



                imageProcessingRunning = false

                val duration = System.currentTimeMillis() - start
                Logging.createLogEntry(
                    Logging.loggingLevel_medium,
                    1100,
                    "Image processed in " + duration + "ms."
                )
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

            //Logging.createLogEntry(40, 1100, "Pipeline started with an image.", originalImage) duplicate
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
