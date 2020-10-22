package de.dali.demonstrator.data.repository

import de.dali.demonstrator.data.local.dao.ImageDao
import de.dali.demonstrator.data.local.entity.ImageEntity
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(private val imageDao: ImageDao) {

    fun insert(image: ImageEntity): Long = imageDao.insert(image)

    fun update(image: ImageEntity) = imageDao.update(image)

    fun delete(image: ImageEntity) = imageDao.delete(image)

    fun deleteAll() = imageDao.deleteAll()

    fun getAllFingerprintsByTestPerson(fingerPrintID: Long): Single<List<ImageEntity>> =
        imageDao.getAllImagesByFingerprints(fingerPrintID)

}