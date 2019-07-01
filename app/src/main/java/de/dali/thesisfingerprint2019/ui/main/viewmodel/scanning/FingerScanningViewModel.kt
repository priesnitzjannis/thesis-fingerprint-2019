package de.dali.thesisfingerprint2019.ui.main.viewmodel.scanning


import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.data.local.entity.ImageEntity
import de.dali.thesisfingerprint2019.data.repository.ImageRepository
import de.dali.thesisfingerprint2019.processing.ProcessingThread
import de.dali.thesisfingerprint2019.processing.QualityAssuranceThread
import de.dali.thesisfingerprint2019.processing.common.RotateFinger
import de.dali.thesisfingerprint2019.ui.base.BaseViewModel
import de.dali.thesisfingerprint2019.utils.Constants.NAME_MAIN_FOLDER
import de.dali.thesisfingerprint2019.utils.Utils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.opencv.core.Mat
import javax.inject.Inject
import javax.inject.Named

class FingerScanningViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    @Named("qualityAssuranceDali") private val qualityAssuranceThread: QualityAssuranceThread,
    @Named("pipelineDali") private val processingThread: ProcessingThread
) : BaseViewModel() {

    lateinit var list: List<Int>
    lateinit var entity: FingerPrintEntity

    var amountOfFinger: Int = 0
        set(value) {
            field = value
            qualityAssuranceThread.amountOfFinger = value
        }

    private var compositeDisposable = CompositeDisposable()

    fun startProcessingPipeline() = qualityAssuranceThread.launch()

    fun stopProcessingPipeline() = qualityAssuranceThread.close()

    fun clearQueue() = qualityAssuranceThread.clearQueue()

    fun sendToPipeline(originalImage: Mat) = qualityAssuranceThread.sendToPipeline(originalImage)

    fun setOnSuccess(callback: (List<Pair<Mat, Double>>) -> Unit) = qualityAssuranceThread.setSuccessCallback(callback)

    fun setOnFailure(callback: (String) -> Unit) = qualityAssuranceThread.setFailureCallback(callback)

    fun setSensorOrientation(sensorOrientation: Int) {
        qualityAssuranceThread.sensorOrientation = sensorOrientation
    }

    fun processImages(
        images: List<Pair<Mat, Double>>,
        onSuccess: (Unit) -> Unit,
        onError: (Throwable) -> Unit
    ) {

        val disposable = Single.fromCallable {
            images.forEachIndexed { index, pair ->
                val processedImage = processingThread.process(pair.first)
                val pathName = "$NAME_MAIN_FOLDER/${entity.personID}/${entity.fingerPrintId}"
                val fileName = "${System.currentTimeMillis()}.jpg"
                val correctionDegree = (processingThread.processingSteps[0] as RotateFinger).correctionAngle

                Utils.saveImage(pathName, fileName, processedImage, 100)

                val imageEntity = ImageEntity(
                    fingerPrintID = entity.fingerPrintId,
                    path = "$pathName/$fileName",
                    biometricalID = list[index],
                    timestamp = System.currentTimeMillis(),
                    width = processedImage.width,
                    height = processedImage.height,
                    edgeDensity = pair.second,
                    correctionDegree = correctionDegree
                )

                imageRepository.insert(imageEntity)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)

        compositeDisposable.add(disposable)

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
