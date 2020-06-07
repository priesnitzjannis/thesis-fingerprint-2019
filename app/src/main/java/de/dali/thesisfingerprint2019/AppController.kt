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
import de.dali.thesisfingerprint2019.processing.Config
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

        modules.add(Module(0, "N/A"))
        modules.add(Module(1, "App General"))
        modules.add(Module(10, "Logging Internal"))
        modules.add(Module(100, "User Action"))
        modules.add(Module(200, "Navigation"))
        modules.add(Module(500, "Utils General"))
        modules.add(Module(1000, "Pipeline General"))
        modules.add(Module(1100, "Acquisition"))
        modules.add(Module(1200, "Detection"))
        modules.add(Module(1300, "Rotation"))
        modules.add(Module(1400, "Segmentation"))
        modules.add(Module(1500, "Enhancement"))
        Logging.init(Logging.loggerValues.logSQLite, 90, "1.0.0", modules, this);
        //Logging.disableImageLogging()


        // TODO
        // check line separator cutoff
        // alternatively one value per message
        // Possible bundle into function, possibly with separate thread (check created delay)
        Logging.createLogEntry(
            20,
            1,
            "Config data for Multi Finger Detection:\nKERNEL_SIZE_FILTER = " + Config.KERNEL_SIZE_FILTER + "\n\nH_LOWER = " + Config.H_LOWER + "\nS_LOWER = " + Config.S_LOWER + "\nV_LOWER = " + Config.V_LOWER + "\n\nH_UPPER = " + Config.H_UPPER + "\nS_UPPER = " + Config.S_UPPER + "\nV_UPPER = " + Config.V_UPPER + "\n\nY_LOWER = " + Config.Y_LOWER + "\nCR_LOWER = " + Config.CR_LOWER + "\nCB_LOWER = " + Config.CB_LOWER + "\n\nY_UPPER = " + Config.Y_UPPER + "\nCR_UPPER = " + Config.CR_UPPER + "\nCB_UPPER = " + Config.CB_UPPER + "\n\nKERNEL_SIZE_FAND = " + Config.KERNEL_SIZE_FAND + "\n\nMIN_AREA_SIZE = " + Config.MIN_AREA_SIZE
        )
        Logging.createLogEntry(
            20,
            1,
            "Config data for Finger Rotation Imprecise:\nPOINT_PAIR_DST = " + Config.POINT_PAIR_DST
        )
        Logging.createLogEntry(
            20,
            1,
            "Config data for Finger Border Detection:\nKERNEL_SIZE_BLUR = " + Config.KERNEL_SIZE_BLUR + "\n\nTHRESHOLD_MAX = " + Config.THRESHOLD_MAX + "\nBLOCKSIZE = " + Config.BLOCKSIZE + "\n\nDILATE_KERNEL_SIZE = " + Config.DILATE_KERNEL_SIZE + "\nDILATE_ITERATIONS = " + Config.DILATE_ITERATIONS + "\n\nERODE_KERNEL_SIZE = " + Config.ERODE_KERNEL_SIZE + "\nERODE_ITERATIONS = " + Config.ERODE_ITERATIONS + "\n\nPIXEL_TO_CROP = " + Config.PIXEL_TO_CROP
        )
        Logging.createLogEntry(
            20,
            1,
            "Config data for Find Finger Tip:\nROW_TO_COL_RATIO = " + Config.ROW_TO_COL_RATIO
        )
        Logging.createLogEntry(
            20,
            1,
            "Config data for Multi QA:\nCENTER_SIZE_X = " + Config.CENTER_SIZE_X + "\nCENTER_SIZE_Y = " + Config.CENTER_SIZE_Y + "\nCENTER_OFFSET_X = " + Config.CENTER_OFFSET_X + "\nCENTER_OFFSET_Y = " + Config.CENTER_OFFSET_Y + "\n\nKERNEL_SIZE_GAUS = " + Config.KERNEL_SIZE_GAUS + "\n\nK_SIZE_SOBEL = " + Config.K_SIZE_SOBEL + "\nSCALE = " + Config.SCALE + "\nDELTA = " + Config.DELTA + "\nGRAD_X = " + Config.GRAD_X + "\nGRAD_Y = " + Config.GRAD_Y
        )
        Logging.createLogEntry(
            20,
            1,
            "Config data for Enhancement:\nCLIP_LIMIT = " + Config.CLIP_LIMIT + "\nCLAHE_ITERATIONS = " + Config.CLAHE_ITERATIONS + "\n\nGAUSSIAN_KERNEL_SIZE_LOW = " + Config.GAUSSIAN_KERNEL_SIZE_LOW + "\nGAUSSIAN_KERNEL_SIZE_HIGH = " + Config.GAUSSIAN_KERNEL_SIZE_HIGH
        )
        Logging.createLogEntry(
            20,
            1,
            "Config data for DEPRECATED:" + System.lineSeparator() + "EDGE_DENS_TRESHOLD = " + Config.EDGE_DENS_TRESHOLD
        )



        Logging.createLogEntry(
            20,
            1,
            "Config data for FD:\nSTEP_SIZE = " + Config.STEP_SIZE
        )

        Logging.createLogEntry(
            20,
            1,
            "Config data for ALL:\nTRESHOLD_RED = " + Config.TRESHOLD_RED
        )


        /** Pattern for config data log entry:
        Logging.createLogEntry(
        20,
        1,
        "Config data for :\n = " + Config. + "\n = " + Config.+ "\n = " + Config. + "\n = " + Config. + "\n = " + Config. + "\n = " + Config. + "\n = " + Config. + "\n = " + Config. + "\n = " + Config.
        )*/
    }
}
