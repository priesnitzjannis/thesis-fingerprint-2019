package de.dali.thesisfingerprint2019.utils

import android.os.Environment
import de.dali.thesisfingerprint2019.data.repository.FingerPrintRepository
import de.dali.thesisfingerprint2019.data.repository.ImageRepository
import de.dali.thesisfingerprint2019.data.repository.TestPersonRepository
import de.dali.thesisfingerprint2019.utils.Constants.NAME_MAIN_FOLDER
import de.dali.thesisfingerprint2019.utils.UpdateDB.ImageType.*
import de.dali.thesisfingerprint2019.utils.Utils.copyFile
import java.io.File

class UpdateDB(
    private val userRepo : TestPersonRepository,
    private val fingerPersonRepository: FingerPrintRepository,
    private val imageRepository: ImageRepository) {

    private enum class ImageType(val nameAsString: String){
        RGB("_rbg.jpg"),
        GRAYSCALE("_gray.jpg"),
        ENHANCED("_enhanced.jpg")
    }

    fun update(){
        val allUser = userRepo.getAllTestPerson()
        val allFingerPrints = fingerPersonRepository.getAllFingerPrints()
        val allImages = imageRepository.getAllImages()

        val extStorage = Environment.getExternalStorageDirectory()

        allUser.forEach { user ->
                val userFingerPrints = allFingerPrints.filter { it.personID == user.personID }

                userFingerPrints.forEach { fingerprint ->
                    val userImages = allImages.filter{it.fingerPrintID == fingerprint.fingerPrintId}

                    userImages.forEach {
                        val fileName = createFileName(
                            user.personID.toString(),
                            if (fingerprint.vendor.contains("HUAWEI", ignoreCase = true)){ "0" } else {"1"} ,
                            it.imageId
                        )

                        val parentPath = "updated-thesis-fingerprint-images"
                        val childPath = "$parentPath/$fileName"

                        handleImage(
                            extStorage = extStorage.absolutePath,
                            pathImageOld = it.pathRGB!!,
                            imagePathNew = childPath,
                            parentPath = parentPath,
                            imageType = RGB
                        )

                        handleImage(
                            extStorage = extStorage.absolutePath,
                            pathImageOld = it.pathGray!!,
                            imagePathNew = childPath,
                            parentPath = parentPath,
                            imageType = GRAYSCALE
                        )

                        handleImage(
                            extStorage = extStorage.absolutePath,
                            pathImageOld = it.pathEnhanced!!,
                            imagePathNew = childPath,
                            parentPath = parentPath,
                            imageType = ENHANCED
                        )

                        it.pathRGB = fileName + RGB.nameAsString
                        it.pathGray = fileName + GRAYSCALE.nameAsString
                        it.pathEnhanced = fileName + ENHANCED.nameAsString

                        imageRepository.update(it)

                    }
                }
            }
        }

    private fun handleImage(extStorage: String,
                            pathImageOld: String,
                            imagePathNew: String,
                            parentPath: String,
                            imageType: ImageType){
        val file = File(extStorage, pathImageOld)
        val fileNew = File(extStorage, imagePathNew + imageType.nameAsString)
        copyFile(file, fileNew, "$extStorage/$parentPath")
    }

    private fun createFileName(vararg partialNames: Any?) = partialNames.joinToString(separator = "_")


}