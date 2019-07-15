package de.dali.thesisfingerprint2019.ui.main.viewmodel.testperson

import de.dali.thesisfingerprint2019.data.local.entity.TestPersonEntity
import de.dali.thesisfingerprint2019.data.repository.TestPersonRepository
import de.dali.thesisfingerprint2019.ui.base.BaseViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TestPersonCreateViewModel @Inject constructor(private val testPersonRepository: TestPersonRepository) :
    BaseViewModel() {

    lateinit var entity: TestPersonEntity
    private var compositeDisposable = CompositeDisposable()

    var name: String = ""
    lateinit var gender: String
    lateinit var color: String
    var age: Int = -1

    fun insertTestPerson(
        person: TestPersonEntity,
        onSuccess: (Long) -> Unit,
        onError: (Throwable) -> Unit
    ) {

        val disposable = Single.fromCallable { testPersonRepository.insert(person) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)

        compositeDisposable.add(disposable)
    }

    fun generateTestPerson() {
        entity = TestPersonEntity(
            name = name,
            gender = gender,
            age = age,
            skinColor = color,
            timestamp = System.currentTimeMillis()
        )
    }

    fun isTestPersonInitialised(): Boolean = ::entity.isInitialized

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}