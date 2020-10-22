package de.dali.demonstrator

import android.app.Activity
import android.app.Application
import com.facebook.stetho.Stetho
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import de.dali.demonstrator.di.component.DaggerAppComponent
import de.dali.demonstrator.di.module.DbModule
import de.dali.demonstrator.logging.Logging
import de.dali.demonstrator.logging.SQLite.Entity.Module
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

        //val start2 = System.currentTimeMillis()
        var modules: ArrayList<Module> = ArrayList<Module>()

        modules.add(Module(0, "N/A"))
        modules.add(Module(1, "App General"))
        modules.add(Module(10, "Logging Internal"))
        modules.add(Module(100, "User Interaction"))
        modules.add(Module(500, "Utils General"))
        modules.add(Module(1000, "Acquisition"))
        modules.add(Module(1100, "Pipeline General"))
        modules.add(Module(1200, "Image Run"))
        modules.add(Module(1300, "Scanning"))

        // Segmentation:
            // Detection:
        modules.add(Module(1400, "Multifinger Detection"))
        modules.add(Module(1500, "Finger Border Detection"))
        modules.add(Module(1600, "Fingertip Location"))
            // Rotation
        modules.add(Module(1700, "Rotation Imprecise"))
        modules.add(Module(1800, "Rotation Precise"))

        // Enhancement
        modules.add(Module(1900, "Enhancement"))
        Logging.init(Logging.loggerValues.logSQLite, Logging.loggingLevel_debug, "1.0.0", modules, this);
        //Logging.disableImageLogging()

        //val duration2 = System.currentTimeMillis() - start2
        //Logging.createLogEntry(
        //    Logging.loggingLevel_critical,
        //    10,
        //    "Initialising the logging module took " + duration2 + "ms."
        //)
    }
}
