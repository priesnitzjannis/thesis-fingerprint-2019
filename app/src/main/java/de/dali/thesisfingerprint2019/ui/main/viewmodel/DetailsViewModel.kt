package de.dali.thesisfingerprint2019.ui.main.viewmodel

import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.ui.base.BaseViewModel
import javax.inject.Inject

class DetailsViewModel @Inject constructor() : BaseViewModel() {

    var entity: FingerPrintEntity? = null

}