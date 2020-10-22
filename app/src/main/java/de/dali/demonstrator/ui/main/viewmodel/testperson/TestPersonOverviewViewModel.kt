package de.dali.demonstrator.ui.main.viewmodel.testperson

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import de.dali.demonstrator.data.local.entity.TestPersonEntity
import de.dali.demonstrator.data.repository.TestPersonRepository
import de.dali.demonstrator.ui.base.BaseViewModel
import de.dali.demonstrator.utils.Utils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

// Testpersonenübersicht
class TestPersonOverviewViewModel @Inject constructor(private val testPersonRepository: TestPersonRepository) :
    BaseViewModel() {

    val listOfTestPerson = MutableLiveData<List<TestPersonEntity>>()
    private var compositeDisposable = CompositeDisposable()

    fun loadTestPerson() {
        val fingerprintDisposable = testPersonRepository.getAllTestPerson()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onFetchComplete, this::onError)
        compositeDisposable.add(fingerprintDisposable)
    }

    fun exportDB(
        context: Context,
        onSuccess: (Unit) -> Unit,
        onError: (Throwable) -> Unit
    ) {

        val disposable = Single.fromCallable {
            Utils.exportDB(context)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)

        compositeDisposable.add(disposable)
    }

    private fun onError(throwable: Throwable) {
        Log.d(TAG, throwable.message ?: "Couldn't load test person.")
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