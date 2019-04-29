package de.dali.thesisfingerprint2019.data.repository


import de.dali.thesisfingerprint2019.data.local.dao.FingerPrintDao
import javax.inject.Singleton


@Singleton
class FingerPrintRepository(private val fingerPrintDao: FingerPrintDao)
