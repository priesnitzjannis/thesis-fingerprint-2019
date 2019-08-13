package de.dali.thesisfingerprint2019.ui.main.viewmodel.testperson

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import de.dali.thesisfingerprint2019.data.local.entity.TestPersonEntity
import de.dali.thesisfingerprint2019.data.repository.TestPersonRepository
import de.dali.thesisfingerprint2019.ui.base.BaseViewModel
import de.dali.thesisfingerprint2019.utils.UpdateDB
import de.dali.thesisfingerprint2019.utils.Utils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TestPersonOverviewViewModel @Inject constructor(private val testPersonRepository: TestPersonRepository) :
    BaseViewModel() {

    val listOfTestPerson = MutableLiveData<List<TestPersonEntity>>()
    private var compositeDisposable = CompositeDisposable()

    @Inject lateinit var updateDB: UpdateDB

    fun loadTestPerson() {
        val fingerprintDisposable = Single.fromCallable {
            testPersonRepository.getAllTestPerson()
        }
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

    fun updateDB(
        onSuccess: (Unit) -> Unit,
        onError: (Throwable) -> Unit
    ){
        val disposable = Single.fromCallable {
            updateDB.update()
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