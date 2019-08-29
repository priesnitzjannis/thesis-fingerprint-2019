package de.dali.thesisfingerprint2019.utils

import android.os.Environment
import de.dali.thesisfingerprint2019.data.repository.FingerPrintRepository
import de.dali.thesisfingerprint2019.data.repository.ImageRepository
import de.dali.thesisfingerprint2019.data.repository.TestPersonRepository
import de.dali.thesisfingerprint2019.utils.UpdateDB.ImageType.*
import de.dali.thesisfingerprint2019.utils.Utils.copyFile
import java.io.File

class UpdateDB(
    private val userRepo : TestPersonRepository,
    private val fingerPersonRepository: FingerPrintRepository,
    private val imageRepository: ImageRepository) {

    private enum class ImageType(val nameAsString: String){
        RGB("_rgb"),
        GRAYSCALE("_gray"),
        ENHANCED("_enhanced")
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
                            if (fingerprint.vendor.contains("HUAWEI", ignoreCase = true)){ "0" } else {"1"},
                            if (it.brokenDetectedByHand == true){"1"} else {"0"},
                            user.personID,
                            it.biometricalID,
                            it.imageId
                        )

                        val parentPath = "updated-thesis-fingerprint-images"
                        val childPathImage = "$parentPath/thesis-images-2019/$fileName"
                        val childPathMnt = "$parentPath/thesis-minutiaes-2019/$fileName"

                        handleFile(
                            extStorage = extStorage.absolutePath,
                            pathImageOld = it.pathRGB!!,
                            imagePathNew = childPathImage,
                            mntPathNew = childPathMnt,
                            parentPath = parentPath,
                            imageType = RGB
                        )

                        handleFile(
                            extStorage = extStorage.absolutePath,
                            pathImageOld = it.pathGray!!,
                            imagePathNew = childPathImage,
                            mntPathNew = childPathMnt,
                            parentPath = parentPath,
                            imageType = GRAYSCALE
                        )

                        handleFile(
                            extStorage = extStorage.absolutePath,
                            pathImageOld = it.pathEnhanced!!,
                            imagePathNew = childPathImage,
                            mntPathNew = childPathMnt,
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

    private fun handleFile(extStorage: String,
                           pathImageOld: String,
                           imagePathNew: String,
                           mntPathNew: String,
                           parentPath: String,
                           imageType: ImageType){

        val file = File(extStorage, "thesis-fingerprints-2019/thesis-images-2019/$pathImageOld")
        val fileNew = File(extStorage, imagePathNew + imageType.nameAsString + ".jpg")
        copyFile(file, fileNew, "$extStorage/$parentPath/thesis-images-2019")

        val fileMnt = File(extStorage, "thesis-fingerprints-2019/thesis-minutiaes-2019/${pathImageOld.replace("rbg","rgb").replace(".jpg",".mnt")}")
        val fileMntNew = File(extStorage, mntPathNew + imageType.nameAsString + ".mnt")
        copyFile(fileMnt, fileMntNew, "$extStorage/$parentPath/thesis-minutiaes-2019")
    }

    private fun createFileName(vararg partialNames: Any?) = partialNames.joinToString(separator = "_")


}