package de.dali.thesisfingerprint2019.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.dali.thesisfingerprint2019.ui.main.fragment.fingerprint.FingerPrintCreateFragment
import de.dali.thesisfingerprint2019.ui.main.fragment.fingerprint.FingerPrintOverViewFragment
import de.dali.thesisfingerprint2019.ui.main.fragment.scanning.FingerScanningFragment
import de.dali.thesisfingerprint2019.ui.main.fragment.settings.SettingsFragment
import de.dali.thesisfingerprint2019.ui.main.fragment.testperson.TestPersonCreateFragment
import de.dali.thesisfingerprint2019.ui.main.fragment.testperson.TestPersonOverviewFragment

@Module
abstract class FragmentModule {

    //region TESTPERSON
    @ContributesAndroidInjector
    abstract fun contributeTestPersonOverviewFragment(): TestPersonOverviewFragment

    @ContributesAndroidInjector
    abstract fun contributeTestPersonCreateFragment(): TestPersonCreateFragment
    //endregion

    //region FINGERPRINT
    @ContributesAndroidInjector
    abstract fun contributeFingerPrintOverViewFragment(): FingerPrintOverViewFragment

    @ContributesAndroidInjector
    abstract fun contributeFingerPrintCreateFragment(): FingerPrintCreateFragment
    //endregion

    //region SETTINGS
    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment
    //endregion

    //region SCANNING
    @ContributesAndroidInjector
    abstract fun contributeFingerScanningFragment(): FingerScanningFragment
    //endregion

}