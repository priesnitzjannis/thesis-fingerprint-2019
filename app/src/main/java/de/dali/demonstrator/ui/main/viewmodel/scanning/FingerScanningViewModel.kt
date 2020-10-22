package de.dali.demonstrator.ui.main.viewmodel.scanning


import de.dali.demonstrator.data.local.entity.FingerPrintEntity
import de.dali.demonstrator.data.local.entity.FingerPrintIntermediateEntity
import de.dali.demonstrator.data.local.entity.ImageEntity
import de.dali.demonstrator.data.repository.FingerPrintRepository
import de.dali.demonstrator.data.repository.ImageRepository
import de.dali.demonstrator.logging.Logging
import de.dali.demonstrator.processing.ProcessingThread
import de.dali.demonstrator.processing.QualityAssuranceThread
import de.dali.demonstrator.processing.QualityAssuranceThread.IntermediateResults
import de.dali.demonstrator.processing.Utils.HAND
import de.dali.demonstrator.processing.Utils.HAND.NOT_SPECIFIED
import de.dali.demonstrator.processing.Utils.convertMatToBitMap
import de.dali.demonstrator.processing.Utils.hasEnoughContent
import de.dali.demonstrator.processing.Utils.hasValidSize
import de.dali.demonstrator.processing.common.RotateFinger
import de.dali.demonstrator.ui.base.BaseViewModel
import de.dali.demonstrator.ui.main.fragment.scanning.FingerScanningFragment
import de.dali.demonstrator.utils.Constants.NAME_MAIN_FOLDER
import de.dali.demonstrator.utils.Utils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.opencv.core.Mat
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Named

// Worflow Akquise
class FingerScanningViewModel @Inject constructor(
    private val fingerPrintRepository: FingerPrintRepository,
    private val imageRepository: ImageRepository,
    @Named("qualityAssuranceDali") private val qualityAssuranceThread: QualityAssuranceThread,
    @Named("pipelineDali") private val processingThread: ProcessingThread
) : BaseViewModel() {

    lateinit var list: List<Int>
    lateinit var entity: FingerPrintEntity

    var sucessfullFingersCounter: Int = 0

    var processedFingers: Int = 0

    var record: Boolean = false

    private var recordSetIDs: String = "recordSetIDs"

    var amountOfFinger: Int = 0
        set(value) {
            field = value
            qualityAssuranceThread.amountOfFinger = value
        }

    var hand: HAND =
        NOT_SPECIFIED
        set(value) {
            field = value
            qualityAssuranceThread.hand = value
        }

    private var compositeDisposable = CompositeDisposable()

    fun startProcessingPipeline() = qualityAssuranceThread.launch()

    fun stopProcessingPipeline() = qualityAssuranceThread.close()

    fun clearQueue() = qualityAssuranceThread.clearQueue()

    fun sendToPipeline(originalImage: Mat) = qualityAssuranceThread.sendToPipeline(originalImage)

    fun setOnSuccess(callback: (List<FingerPrintIntermediateEntity>) -> Unit) =
        qualityAssuranceThread.setSuccessCallback(callback)

    fun setOnUpdate(callback: (IntermediateResults, String, Int) -> Unit) =
        qualityAssuranceThread.setUpdateCallback(callback)

    fun setSensorOrientation(sensorOrientation: Int) {
        qualityAssuranceThread.sensorOrientation = sensorOrientation
    }

    fun processImages(
        images: List<FingerPrintIntermediateEntity>,
        onSuccess: (Unit) -> Unit,
        onError: (Throwable) -> Unit
    ) {

        val disposable = Single.fromCallable {
            val id = fingerPrintRepository.insert(entity)

            entity.fingerPrintId = id

            
            var textGet: MutableMap<Int, Int> = FingerScanningFragment().getRecordSetIDs(NAME_MAIN_FOLDER)
            var nextRecordID: Int = 0

            if (textGet.containsKey(entity.personID.toInt())){
                nextRecordID = textGet[entity.personID.toInt()]!!
                textGet[entity.personID.toInt()] = nextRecordID + 1
            } else {
              textGet[entity.personID.toInt()] = 1
                nextRecordID = 1
                textGet[entity.personID.toInt()] = nextRecordID + 1
            }

            FingerScanningFragment().storeRecordSetIDs(textGet, NAME_MAIN_FOLDER)
            
            images.forEachIndexed { index, pair ->
                val pathName = NAME_MAIN_FOLDER
                val timestamp = System.currentTimeMillis()

                // TestpersonID_RecordSetID_biometrischeFingerID_(FingerID)
                var baseFileName = entity.personID.toString() + "_" + nextRecordID + "_" + list[index]

                val fileName = baseFileName + ".jpg"
                val fileNameOriginal = baseFileName +"_orig.jpg"
                val fileNameGray = baseFileName + "_gray.jpg"

                Utils.saveImage(pathName, fileNameOriginal, convertMatToBitMap(pair.mat)!!, 100)

                val processedImage = processingThread.process(pair.mat)
                val correctionDegree = (qualityAssuranceThread.processingStep[3] as RotateFinger).correctionAngle

                val grayBmp = convertMatToBitMap(processingThread.grayMat)

                processedFingers += 1

                Utils.saveImage(pathName, fileNameGray, grayBmp!!, 100)
                Utils.saveImage(pathName, fileName, processedImage, 100)

                val imageEntity = ImageEntity(
                    fingerPrintID = entity.fingerPrintId,
                    pathEnhanced = "$pathName/$fileName",
                    pathGray = "$pathName/$fileNameGray",
                    pathRGB = "$pathName/$fileNameOriginal",
                    biometricalID = list[index],
                    timestamp = timestamp,
                    width = processedImage.width,
                    height = processedImage.height,
                    edgeDensity = pair.edgeDens,
                    brokenDetectedByAlgorithm = !hasValidSize(pair.mat) || !hasEnoughContent(pair.mat),
                    correctionDegree = (pair.correctionDegreeImprecise + correctionDegree).toBigDecimal().setScale(
                        2,
                        RoundingMode.UP
                    ).toDouble()
                )

                imageRepository.insert(imageEntity)
            }
            Logging.completeAcquisition(id)
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
