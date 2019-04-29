package de.dali.thesisfingerprint2019

import android.app.Activity
import android.app.Application
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import de.dali.thesisfingerprint2019.di.component.DaggerAppComponent
import de.dali.thesisfingerprint2019.di.module.DbModule
import javax.inject.Inject

class AppController : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
            .application(this)
            .dbModule(DbModule())
            .build()
            .inject(this)
    }
}
