package de.dali.demonstrator.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import de.dali.demonstrator.factory.ViewModelFactory
import de.dali.demonstrator.ui.main.viewmodel.fingerprint.FingerPrintCreateViewModel
import de.dali.demonstrator.ui.main.viewmodel.fingerprint.FingerPrintOverviewViewModel
import de.dali.demonstrator.ui.main.viewmodel.scanning.FingerScanningViewModel
import de.dali.demonstrator.ui.main.viewmodel.testperson.TestPersonCreateViewModel
import de.dali.demonstrator.ui.main.viewmodel.testperson.TestPersonOverviewViewModel

@Module
internal abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    //region TESTPERSON
    @Binds
    @IntoMap
    @ViewModelKey(TestPersonOverviewViewModel::class)
    protected abstract fun testPersonOverViewModel(testPersonOverviewViewModel: TestPersonOverviewViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TestPersonCreateViewModel::class)
    protected abstract fun testPersonCreateViewModel(testPersonCreateViewModel: TestPersonCreateViewModel): ViewModel
    //endregion

    //region FINGERPRINT
    @Binds
    @IntoMap
    @ViewModelKey(FingerPrintOverviewViewModel::class)
    protected abstract fun fingerPrintOverviewViewModel(fingerPrintOverviewViewModel: FingerPrintOverviewViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FingerPrintCreateViewModel::class)
    protected abstract fun fingerPrintCreateViewModel(fingerPrintCreateViewModel: FingerPrintCreateViewModel): ViewModel
    //endregion

    //region SCANNING
    @Binds
    @IntoMap
    @ViewModelKey(FingerScanningViewModel::class)
    protected abstract fun fingerScanningViewModel(fingerScanningViewModel: FingerScanningViewModel): ViewModel
    //endregion

}