package de.dali.thesisfingerprint2019.ui.main.viewmodel.fingerprint

import android.util.Log
import androidx.lifecycle.MutableLiveData
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.data.local.entity.TestPersonEntity
import de.dali.thesisfingerprint2019.data.repository.FingerPrintRepository
import de.dali.thesisfingerprint2019.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FingerPrintOverviewViewModel @Inject constructor(private val fingerPrintRepository: FingerPrintRepository) :
    BaseViewModel() {

    lateinit var entity: TestPersonEntity

    val listOfFingerPrints = MutableLiveData<List<FingerPrintEntity>>()
    private var compositeDisposable = CompositeDisposable()

    fun loadFingerPrints(personID: Long) {
        val fingerprintDisposable = fingerPrintRepository.getAllFingerprintsByTestPerson(personID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onFingerPrintsFetched, this::onError)
        compositeDisposable.add(fingerprintDisposable)
    }

    private fun onError(throwable: Throwable) {
        Log.d(TAG, throwable.message ?: "Couldn't load fingerprints.")
    }

    private fun onFingerPrintsFetched(fingerprints: List<FingerPrintEntity>) {
        listOfFingerPrints.value = fingerprints
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    companion object {
        val TAG = FingerPrintOverviewViewModel::class.java.simpleName
    }

}