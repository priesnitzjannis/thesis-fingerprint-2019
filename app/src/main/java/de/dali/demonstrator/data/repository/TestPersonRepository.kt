package de.dali.demonstrator.data.repository

import de.dali.demonstrator.data.local.dao.TestPersonDao
import de.dali.demonstrator.data.local.entity.TestPersonEntity
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestPersonRepository @Inject constructor(private val testPersonDao: TestPersonDao) {

    fun insert(testPerson: TestPersonEntity): Long = testPersonDao.insert(testPerson)

    fun update(testPerson: TestPersonEntity) = testPersonDao.update(testPerson)

    fun delete(testPerson: TestPersonEntity) = testPersonDao.delete(testPerson)

    fun deleteAll() = testPersonDao.deleteAll()

    fun getAllTestPerson(): Single<List<TestPersonEntity>> = testPersonDao.getAllTestPerson()

}
