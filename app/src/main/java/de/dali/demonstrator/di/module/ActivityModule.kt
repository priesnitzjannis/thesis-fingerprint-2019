package de.dali.demonstrator.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.dali.demonstrator.ui.main.activity.MainActivity

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeMainActivity(): MainActivity

}