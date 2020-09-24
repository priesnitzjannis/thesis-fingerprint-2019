package de.dali.thesisfingerprint2019.ui.main.viewmodel.fingerprint

import android.util.Log
import androidx.lifecycle.MutableLiveData
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.data.local.entity.ImageEntity
import de.dali.thesisfingerprint2019.data.local.entity.TestPersonEntity
import de.dali.thesisfingerprint2019.data.repository.FingerPrintRepository
import de.dali.thesisfingerprint2019.data.repository.ImageRepository
import de.dali.thesisfingerprint2019.ui.base.BaseViewModel
import de.dali.thesisfingerprint2019.ui.main.viewmodel.testperson.TestPersonOverviewViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

// Neuen Fingerprint aufnehmen - Einstellungen
class FingerPrintCreateViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) :
    BaseViewModel() {

    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    lateinit var testPersonEntity: TestPersonEntity
    lateinit var fingerPrintEntity: FingerPrintEntity

    var personID: Long = 0L
    var location: String = ""
    var illumination: Float = 0F
    var vendor: String = ""

    var selectedFinger: MutableLiveData<MutableList<Int>> = MutableLiveData()

    val listOfImages = MutableLiveData<List<ImageEntity>>()



    init {
        selectedFinger.value = mutableListOf()
    }

    fun loadImages(fingerPrintID: Long) {
        val disposable = imageRepository.getAllFingerprintsByTestPerson(fingerPrintID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onFetchComplete, this::onError)

        compositeDisposable.add(disposable)

    }

    private fun onError(throwable: Throwable) {
        Log.d(TestPersonOverviewViewModel.TAG, throwable.message)
    }

    private fun onFetchComplete(testPerson: List<ImageEntity>) {
        listOfImages.value = testPerson
    }

    fun isEntityInitialized(): Boolean = ::fingerPrintEntity.isInitialized

    fun createFingerPrintEntity() {
        fingerPrintEntity = FingerPrintEntity(
            personID = personID,
            location = location,
            illumination = illumination,
            vendor = vendor,
            timestamp = System.currentTimeMillis()
        )
    }

    fun getSortedStringList(): List<Int>? {
        if (selectedFinger.value?.none { it > 5 } == true) {
            selectedFinger.value?.sortDescending()
        } else {
            selectedFinger.value?.sort()
        }
        return selectedFinger.value
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


    companion object {
        val TAG = FingerPrintCreateViewModel::class.java.simpleName
    }

}