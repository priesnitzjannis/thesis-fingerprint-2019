package de.dali.thesisfingerprint2019.ui.main.viewmodel.fingerprint

import androidx.lifecycle.MutableLiveData
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.data.local.entity.TestPersonEntity
import de.dali.thesisfingerprint2019.data.repository.FingerPrintRepository
import de.dali.thesisfingerprint2019.ui.base.BaseViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FingerPrintCreateViewModel @Inject constructor(val fingerPrintRepository: FingerPrintRepository) :
    BaseViewModel() {

    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    lateinit var testPersonEntity: TestPersonEntity
    lateinit var fingerPrintEntity: FingerPrintEntity

    var personID: Long = 0L
    var location: String = ""
    var illumination: Float = 0F
    var vendor: String = ""

    var selectedFinger: MutableLiveData<MutableList<Int>> = MutableLiveData()

    init {
        selectedFinger.value = mutableListOf()
    }

    fun insertFingerPrint(
        fingerPrintEntity: FingerPrintEntity,
        onSuccess: (Long) -> Unit,
        onError: (Throwable) -> Unit
    ) {

        val disposable = Single.fromCallable { fingerPrintRepository.insert(fingerPrintEntity) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)

        compositeDisposable.add(disposable)
    }

    fun isEntityInitialized(): Boolean = ::fingerPrintEntity.isInitialized

    fun createFingerPrintEntity() {
        fingerPrintEntity = FingerPrintEntity(
            personID = personID,
            location = location,
            illumination = illumination,
            vendor = vendor,
            listOfFingerIds = getSortedStringList(),
            timestamp = System.currentTimeMillis()
        )
    }

    private fun getSortedStringList(): List<String> {
        selectedFinger.value?.sort()
        return selectedFinger.value?.map { it.toString() } ?: listOf()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}