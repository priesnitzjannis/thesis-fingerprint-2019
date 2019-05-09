package de.dali.thesisfingerprint2019.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.dali.thesisfingerprint2019.ui.main.fragment.DetailsFragment
import de.dali.thesisfingerprint2019.ui.main.fragment.FingerScanningFragment
import de.dali.thesisfingerprint2019.ui.main.fragment.SelectionFragment

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeFingerScanningFragment(): FingerScanningFragment

    @ContributesAndroidInjector
    abstract fun contributeDetailsFragment(): DetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeSelectionFragment(): SelectionFragment

}