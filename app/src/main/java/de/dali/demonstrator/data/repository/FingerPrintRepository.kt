package de.dali.demonstrator.data.repository

import de.dali.demonstrator.data.local.dao.FingerPrintDao
import de.dali.demonstrator.data.local.entity.FingerPrintEntity
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FingerPrintRepository @Inject constructor(private val fingerPrintDao: FingerPrintDao) {

    fun insert(fingerprint: FingerPrintEntity): Long = fingerPrintDao.insert(fingerprint)

    fun update(fingerprint: FingerPrintEntity) = fingerPrintDao.update(fingerprint)

    fun delete(fingerprint: FingerPrintEntity) = fingerPrintDao.delete(fingerprint)

    fun deleteAll() = fingerPrintDao.deleteAll()

    fun getAllFingerprintsByTestPerson(personID: Long): Single<List<FingerPrintEntity>> =
        fingerPrintDao.getAllFingerprintsByTestPerson(personID)

}
