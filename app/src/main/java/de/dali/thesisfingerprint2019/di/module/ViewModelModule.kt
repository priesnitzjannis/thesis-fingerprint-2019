package de.dali.thesisfingerprint2019.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import de.dali.thesisfingerprint2019.factory.ViewModelFactory
import de.dali.thesisfingerprint2019.ui.main.viewmodel.FingerScanningViewModel

@Module
internal abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(FingerScanningViewModel::class)
    protected abstract fun movieListViewModel(moviesListViewModel: FingerScanningViewModel): ViewModel

}