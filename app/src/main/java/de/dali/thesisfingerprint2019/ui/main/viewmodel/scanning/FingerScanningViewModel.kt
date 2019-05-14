package de.dali.thesisfingerprint2019.ui.main.viewmodel.scanning


import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.data.repository.FingerPrintRepository
import de.dali.thesisfingerprint2019.processing.ProcessingPipeline
import de.dali.thesisfingerprint2019.ui.base.BaseViewModel
import de.dali.thesisfingerprint2019.ui.base.custom.ResultView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.opencv.core.Mat
import javax.inject.Inject

class FingerScanningViewModel @Inject constructor(
    val fingerPrintRepository: FingerPrintRepository,
    val processingPipeline: ProcessingPipeline
) : BaseViewModel() {

    lateinit var entity: FingerPrintEntity

    var compositeDisposable: CompositeDisposable = CompositeDisposable()

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

    fun insertTestPerson(
        fingerprint: FingerPrintEntity,
        onSuccess: (Long) -> Unit,
        onError: (Throwable) -> Unit
    ) {

        val disposable = Single.fromCallable { fingerPrintRepository.insert(fingerprint) }
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
