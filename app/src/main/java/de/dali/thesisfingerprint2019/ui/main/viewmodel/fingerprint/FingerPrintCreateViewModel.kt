package de.dali.thesisfingerprint2019.ui.main.viewmodel.fingerprint

import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.data.local.entity.TestPersonEntity
import de.dali.thesisfingerprint2019.data.repository.FingerPrintRepository
import de.dali.thesisfingerprint2019.ui.base.BaseViewModel
import javax.inject.Inject

class FingerPrintCreateViewModel @Inject constructor(val fingerPrintRepository: FingerPrintRepository) :
    BaseViewModel() {

    lateinit var testPersonEntity: TestPersonEntity
    lateinit var fingerPrintEntity: FingerPrintEntity

    var personID: Long = 0L
    var location: String = ""
    var illumination: Float = 0F
    var vendor: String = ""

    var thumbIdx: String? = null
    var indexIdx: String? = null
    var middleIdx: String? = null
    var ringIdx: String? = null
    var littleIdx: String? = null

    var selectedFinger: List<String> = mutableListOf(
        thumbIdx,
        indexIdx,
        middleIdx,
        ringIdx,
        littleIdx
    ).filterNotNull()

    fun createFingerPrintEntity(): FingerPrintEntity {
        return FingerPrintEntity(
            personID = personID,
            location = location,
            illumination = illumination,
            vendor = vendor,
            listOfFingerIds = selectedFinger
        )
    }
}