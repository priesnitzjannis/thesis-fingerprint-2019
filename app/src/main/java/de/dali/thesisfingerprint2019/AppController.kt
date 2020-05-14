package de.dali.thesisfingerprint2019

import android.app.Activity
import android.app.Application
import com.facebook.stetho.Stetho
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import de.dali.thesisfingerprint2019.di.component.DaggerAppComponent
import de.dali.thesisfingerprint2019.di.module.DbModule
import de.dali.thesisfingerprint2019.logging.Logging
import de.dali.thesisfingerprint2019.logging.SQLite.Entity.Module
import javax.inject.Inject

class AppController : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }

        DaggerAppComponent.builder()
            .application(this)
            .dbModule(DbModule())
            .build()
            .inject(this)


        var modules: ArrayList<Module> = ArrayList<Module>()

        modules.add(Module(0, "Logging"))
        modules.add(Module(1, "App Start/Stop"))
        modules.add(Module(2, "App General"))
        Logging.getLoggingLevel();
        Logging.init(Logging.loggerValues.logSQLite, 100, "1.0.0", modules, this);
    }
}
