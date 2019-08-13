package de.dali.thesisfingerprint2019.utils

import android.os.Environment
import de.dali.thesisfingerprint2019.data.repository.FingerPrintRepository
import de.dali.thesisfingerprint2019.data.repository.ImageRepository
import de.dali.thesisfingerprint2019.data.repository.TestPersonRepository
import de.dali.thesisfingerprint2019.utils.Constants.NAME_MAIN_FOLDER
import de.dali.thesisfingerprint2019.utils.Utils.copyFile
import java.io.File

class UpdateDB(val userRepo : TestPersonRepository,
               val fingerPersonRepository: FingerPrintRepository,
               val imageRepository: ImageRepository) {

    fun update(){
        val allUser = userRepo.getAllTestPerson()
        val allFingerPrints = fingerPersonRepository.getAllFingerPrints()
        val allImages = imageRepository.getAllImages()

        val listOfDevices = listOf("Samsung" /*, "Huawei" */)

        listOfDevices.forEach { deviceName ->
            allUser.forEach { user ->
                val userFingerPrints = allFingerPrints.filter { it.personID == user.personID && it.vendor.contains(deviceName, ignoreCase = true)}

                userFingerPrints.forEach { fingerprint ->
                    val userImages = allImages.filter{it.fingerPrintID == fingerprint.fingerPrintId}

                    val filePath = user.timestamp.toString() + "/" + fingerprint.fingerPrintId
                    val extStorage = Environment.getExternalStorageDirectory()

                    userImages.forEach {
                        val fileName = user.personID.toString() + "_" + if (fingerprint.vendor.contains("HUAWEI", ignoreCase = true)){ "0" } else {"1"} + "_" + it.imageId
                        val childPath = "updated-thesis-fingerprint-images/$filePath/$fileName"

                        val fileRGB = File(extStorage, it.pathRGB)
                        val fileRGBNew = File(extStorage, "${childPath}_rgb.jpg")
                        copyFile(fileRGB, fileRGBNew, extStorage.absolutePath + "/updated-thesis-fingerprint-images/$filePath")

                        val fileGray = File(extStorage, it.pathGray)
                        val fileGrayNew = File(extStorage, "${childPath}_gray.jpg")
                        copyFile(fileGray, fileGrayNew, extStorage.absolutePath + "/updated-thesis-fingerprint-images/$filePath")

                        val fileEnhanced = File(extStorage, it.pathEnhanced)
                        val fileEnhancedNew = File(extStorage, "${childPath}_enhanced.jpg")
                        copyFile(fileEnhanced, fileEnhancedNew,extStorage.absolutePath + "/updated-thesis-fingerprint-images/$filePath")

                        it.pathRGB = NAME_MAIN_FOLDER + "/$filePath/${fileName}_rgb.jpg"
                        it.pathGray = NAME_MAIN_FOLDER + "/$filePath/${fileName}__gray.jpg"
                        it.pathEnhanced =  NAME_MAIN_FOLDER + "/$filePath/${fileName}__enhanced.jpg"

                        imageRepository.update(it)

                    }
                }
            }
        }

    }


}