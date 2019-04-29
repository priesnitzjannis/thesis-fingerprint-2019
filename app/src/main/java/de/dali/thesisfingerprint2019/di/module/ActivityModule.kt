package de.dali.thesisfingerprint2019.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.dali.thesisfingerprint2019.ui.main.activity.MainActivity

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeMainActivity(): MainActivity

}