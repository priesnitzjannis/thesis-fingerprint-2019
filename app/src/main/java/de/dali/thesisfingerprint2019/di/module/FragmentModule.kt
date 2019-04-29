package de.dali.thesisfingerprint2019.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.dali.thesisfingerprint2019.ui.main.fragment.FingerScanningFragment

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeMovieListFragment(): FingerScanningFragment

}