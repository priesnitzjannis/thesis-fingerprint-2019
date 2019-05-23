package de.dali.thesisfingerprint2019.ui.main.viewmodel.scanning


import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.data.repository.FingerPrintRepository
import de.dali.thesisfingerprint2019.processing.ProcessingThread
import de.dali.thesisfingerprint2019.processing.QualityAssuranceThread
import de.dali.thesisfingerprint2019.ui.base.BaseViewModel
import de.dali.thesisfingerprint2019.utils.Constants.NAME_MAIN_FOLDER
import de.dali.thesisfingerprint2019.utils.Utils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.opencv.core.Mat
import javax.inject.Inject

class FingerScanningViewModel @Inject constructor(
    private val fingerPrintRepository: FingerPrintRepository,
    private val qualityAssuranceThread: QualityAssuranceThread,
    private val processingThread: ProcessingThread
) : BaseViewModel() {

    lateinit var entity: FingerPrintEntity

    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun startProcessingPipeline() = qualityAssuranceThread.launch()

    fun stopProcessingPipeline() = qualityAssuranceThread.close()

    fun clearQueue() = qualityAssuranceThread.clearQueue()

    fun sendToPipeline(originalImage: Mat) = qualityAssuranceThread.sendToPipeline(originalImage)

    fun setCallback(callback: (Mat) -> Unit) = qualityAssuranceThread.setCallback(callback)

    fun setSensorOrientation(sensorOrientation: Int) {
        qualityAssuranceThread.sensorOrientation = sensorOrientation
    }

    fun processImage(
        image: Mat,
        onSuccess: (Unit) -> Unit,
        onError: (Throwable) -> Unit
    ) {

        val disposable = Single.fromCallable {
            val bmps = processingThread.process(image)
            val imageList = Utils.saveImages(
                NAME_MAIN_FOLDER,
                entity.personID.toString(),
                entity.id.toString(),
                bmps,
                100
            )

            entity.imageList = imageList

            val id = fingerPrintRepository.insert(entity)

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
