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

}