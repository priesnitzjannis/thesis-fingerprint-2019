package de.dali.thesisfingerprint2019.ui.main.viewmodel

import de.dali.thesisfingerprint2019.data.local.dao.FingerPrintDao
import de.dali.thesisfingerprint2019.ui.base.BaseViewModel
import javax.inject.Inject

class FingerScanningViewModel @Inject constructor(fingerPrintDao: FingerPrintDao) : BaseViewModel() {

}
