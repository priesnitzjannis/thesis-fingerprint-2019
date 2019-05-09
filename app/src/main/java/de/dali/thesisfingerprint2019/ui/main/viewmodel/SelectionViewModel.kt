package de.dali.thesisfingerprint2019.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.data.repository.FingerPrintRepository
import de.dali.thesisfingerprint2019.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class SelectionViewModel @Inject constructor(val fingerPrintRepository: FingerPrintRepository) : BaseViewModel() {

    val listOfFingerPrints = MutableLiveData<List<FingerPrintEntity>>()
    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun loadFingerPrints() {
        val fingerprintDisposable = fingerPrintRepository.getAllFingerprints()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onFingerPrintsFetched, this::onError)
        compositeDisposable.add(fingerprintDisposable)
    }

    private fun onError(throwable: Throwable) {
        Log.d(TAG, throwable.message)
    }

    private fun onFingerPrintsFetched(fingerprints: List<FingerPrintEntity>) {
        listOfFingerPrints.value = fingerprints
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    companion object {
        val TAG = SelectionViewModel::class.java.simpleName
    }

}