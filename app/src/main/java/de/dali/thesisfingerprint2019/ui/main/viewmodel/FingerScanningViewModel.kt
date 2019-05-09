package de.dali.thesisfingerprint2019.ui.main.viewmodel

import de.dali.thesisfingerprint2019.data.repository.FingerPrintRepository
import de.dali.thesisfingerprint2019.processing.ProcessingPipeline
import de.dali.thesisfingerprint2019.ui.base.BaseViewModel
import de.dali.thesisfingerprint2019.ui.base.custom.ResultView
import org.opencv.core.Mat
import javax.inject.Inject

class FingerScanningViewModel @Inject constructor(
    val fingerPrintRepository: FingerPrintRepository,
    val processingPipeline: ProcessingPipeline
) : BaseViewModel() {

    fun startProcessingPipeline() {
        processingPipeline.launch()
    }

    fun stopProcessingPipeline() {
        processingPipeline.close()
    }

    fun clearQueue() {
        processingPipeline.clearQueue()
    }

    fun processImage(originalImage: Mat) {
        processingPipeline.processImage(originalImage)
    }

    fun setViews(vararg views: ResultView) {
        processingPipeline.setViews(views)
    }

    fun setSensorOrientation(sensorOrientation: Int) {
        processingPipeline.sensorOrientation = sensorOrientation
    }
}
