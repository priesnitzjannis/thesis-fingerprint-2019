package de.dali.thesisfingerprint2019.ui.main.viewmodel.testperson

import android.util.Log
import androidx.lifecycle.MutableLiveData
import de.dali.thesisfingerprint2019.data.local.entity.TestPersonEntity
import de.dali.thesisfingerprint2019.data.repository.TestPersonRepository
import de.dali.thesisfingerprint2019.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TestPersonOverviewViewModel @Inject constructor(val testPersonRepository: TestPersonRepository) :
    BaseViewModel() {

    val listOfTestPerson = MutableLiveData<List<TestPersonEntity>>()
    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun loadTestPerson() {
        val fingerprintDisposable = testPersonRepository.getAllTestPerson()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onFetchComplete, this::onError)
        compositeDisposable.add(fingerprintDisposable)
    }

    private fun onError(throwable: Throwable) {
        Log.d(TAG, throwable.message)
    }

    private fun onFetchComplete(testPerson: List<TestPersonEntity>) {
        listOfTestPerson.value = testPerson
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    companion object {
        val TAG = TestPersonOverviewViewModel::class.java.simpleName
    }

}