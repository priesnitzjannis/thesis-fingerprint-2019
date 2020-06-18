package de.dali.thesisfingerprint2019.logging

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.util.Log
import de.dali.thesisfingerprint2019.logging.SQLite.Entity.*
import de.dali.thesisfingerprint2019.logging.SQLite.LoggingDatabase
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import kotlin.math.log

class LogSQLite {
    private var SQLDB: LoggingDatabase? = null
    var phone = Phone(Build.MODEL)
    var appVersion = "Placeholder"
    var loggingModuleID: Long = 0

    fun init(_appVersion: String, _loggingModuleID: Long, context: Context?) {
        SQLDB = LoggingDatabase.getInstance(context)
        appVersion = _appVersion
        loggingModuleID = _loggingModuleID

        var canProceed: Boolean = false

        Thread(Runnable {
            Thread.MAX_PRIORITY
            //SQLDB!!.clearAllTables() // Clear the database, should only be temporary

            // Before inserting the current phone, check if it is already in the Database
            var phoneList = SQLDB!!.phoneDao().getPhoneByModel(Build.MODEL)

            if (phoneList.size == 0) {
                // If there is no phone with the given model, save it
                phone.phoneID = SQLDB!!.phoneDao().insertPhone(phone);
            } else {
                var firstPhone: Boolean = true
                // Use the first phone as the phone that being run on
                // If there are more than one phone with the current model, delete all but the first
                for (currentPhone in phoneList) {
                    if (firstPhone) {
                        firstPhone = false
                        phone = currentPhone
                    } else {
                        SQLDB!!.phoneDao().deletePhone(currentPhone)
                    }
                }
            }

            canProceed = true

        }).start()

        // Make sure the thread to add the phone is completed, otherwise writing a logging message may lead to a failure to comply with a foreign key constraint upon which the app exits
        while (!canProceed) {
            Thread.yield()
        }
    }

    fun addModule(module: Module) {
        var canProceed: Boolean = false

        Thread(Runnable {
            Thread.MAX_PRIORITY

            var moduleList = SQLDB!!.moduleDao().getModuleByName(module.name)

            // Before inserting a given module, check if it is already in the Database
            if (moduleList.size == 0) {
                // If there is no module with the given name, save it
                SQLDB!!.moduleDao().insertModule(module);
            } else {
                var needToSave: Boolean = true
                for (currentModule in moduleList) {
                    // We know that the name matches, so we only have to check the ID. If this differs, delete it
                    // If we find a module where the ID matches, we don't need to save the current one
                    if (currentModule.moduleID != module.moduleID) {
                        SQLDB!!.moduleDao().deleteModule(currentModule)
                    } else {
                        needToSave = false
                    }
                }
                if (needToSave) {
                    SQLDB!!.moduleDao().insertModule(module)
                }
            }
            canProceed = true
        }).start()

        // Make sure the thread to add the modules is completed, otherwise writing a logging message may lead to a failure to comply with a foreign key constraint upon which the app exits
        while (!canProceed) {
            Thread.yield()
        }
    }

    /**
     * Creates a log entry
     */
    fun createLogEntry(
        loggingLevel: Long,
        moduleID: Long,
        message: String,
        timestamp: String
    ): Boolean {
        // TODO
        // See if you can get a return code from the other thread
        // could introduce problems if the main threads waits

        Thread(Runnable {
            val loggingMessage =
                LoggingMessage(
                    appVersion!!,
                    phone.phoneID,
                    loggingLevel,
                    moduleID,
                    message,
                    timestamp
                )

            println(
                "New logging message, ID #" + SQLDB!!.loggingMessageDao()
                    .insertLoggingMessage(loggingMessage)
            )

            println(loggingMessage.message)
            //println(""" ${SQLDB!!.loggingMessageDao().all.last().message} """.trimIndent())

        }).start()
        return true
    }

    /**
     * Creates a log entry
     */
    fun createLogEntry(
        loggingLevel: Long,
        moduleID: Long,
        message: String,
        timestamp: String,
        runID: Long
    ): Boolean {
        // TODO
        // See if you can get a return code from the other thread
        // could introduce problems if the main threads waits

        Thread(Runnable {
            val loggingMessage =
                LoggingMessage(
                    appVersion!!,
                    phone.phoneID,
                    loggingLevel,
                    moduleID,
                    message,
                    timestamp
                )

            loggingMessage.runID = runID

            println(
                "New logging message, ID #" + SQLDB!!.loggingMessageDao()
                    .insertLoggingMessage(loggingMessage)
            )

            println(loggingMessage.message)
            //println(""" ${SQLDB!!.loggingMessageDao().all.last().message} """.trimIndent())

        }).start()
        return true
    }

    /**
     * Creates a log entry
     */
    fun createLogEntry(
        loggingLevel: Long,
        moduleID: Long,
        message: String,
        timestamp: String,
        imageMat: Mat
    ): Boolean {
        // TODO
        // See if you can get a return code from the other thread
        // could introduce problems if the main threads waits

        Thread(Runnable {

            val loggingMessage =
                LoggingMessage(
                    appVersion!!, phone.phoneID,
                    loggingLevel,
                    moduleID,
                    message,
                    timestamp
                )

            loggingMessage.imageID = saveImage(imageMat, timestamp)


            println(
                "New logging message, ID #" + SQLDB!!.loggingMessageDao()
                    .insertLoggingMessage(loggingMessage)
            )

            println(loggingMessage.message)
        }).start()
        return true
    }

    /**
     * Creates a log entry
     */
    fun createLogEntry(
        loggingLevel: Long,
        moduleID: Long,
        message: String,
        timestamp: String,
        runID: Long,
        imageMat: Mat
    ): Boolean {
        // TODO
        // See if you can get a return code from the other thread
        // could introduce problems if the main threads waits

        Thread(Runnable {

            val loggingMessage =
                LoggingMessage(
                    appVersion!!, phone.phoneID,
                    loggingLevel,
                    moduleID,
                    message,
                    timestamp
                )
            loggingMessage.runID = runID

            loggingMessage.imageID = saveImage(imageMat, timestamp)

            println(
                "New logging message, ID #" + SQLDB!!.loggingMessageDao()
                    .insertLoggingMessage(loggingMessage)
            )

            println(loggingMessage.message)
        }).start()
        return true
    }

    fun startRun(): Long {
        var runID: Long? = null
        var canProceed: Boolean = false
        Thread(Runnable {
            var run = Run()
            runID = SQLDB!!.runDao().insertRun(run)
            canProceed = true
        }).start()

        while (!canProceed) {
            Thread.yield()
        }
        return runID!!
    }

    fun endRun(runID: Long, returnCode: Int) {
        if (runID == null) {
            return
        }
        Thread(Runnable {
            var runs = SQLDB!!.runDao().getRunByID(runID)

            if (runs.size == 1) {
                runs[0].end(returnCode)
                SQLDB!!.runDao().updateRun(runs[0])
            }
        }).start()
        return
    }


    // ---------------------------------------------------------------------------------------------


    fun saveImage(imageMat: Mat, timestamp: String): Long? {
        if (imageMat.height() == 0 || imageMat.width() == 0) {
            Logging.createLogEntry(Logging.loggingLevel_debug, loggingModuleID, "Cannot save image of size 0.")
            return null
        }

        try {
            //println("Saving image...")

            val img = Image(imageMat.height(), imageMat.width(), ".jpg", timestamp)

            img.imageID = SQLDB!!.imageDao().insertImage(img)

            //println("Added image data to database")

            saveImageToGallery(
                "thesis-fingerprints-2019/Logging-Images",
                img.filename,
                convertMatToBitMap(imageMat)!!,
                100
            )

            //println("image saved")

            return img.imageID
        } catch (e: Exception) {
            //println("Error in saving image: " + e.message)
            Logging.createLogEntry(Logging.loggingLevel_debug, loggingModuleID, "Cannot save image.")
            Log.d("Exception", e.message)
            return null
        }
    }

    fun convertMatToBitMap(input: Mat): Bitmap? {
        if (input == null) {
            //println("Cannot save image, cannot convert null to bitmap")
            return null
        }

        var bmp: Bitmap? = null
        val rgb = Mat()
        //println("initialised null bitmap")

        try {
            Imgproc.cvtColor(input, rgb, Imgproc.COLOR_BGR2RGBA, 4)
            //println("performed color conversion")

            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888)
            //println("created empty bitmap")

            org.opencv.android.Utils.matToBitmap(input, bmp, true)
            //println("finished bitmap conversion")
        } catch (e: Exception) {
            //println("Error in bitmap conversion: " + e.message)
            Logging.createLogEntry(Logging.loggingLevel_debug, loggingModuleID, "Cannot save image.")
            Log.d("Exception", e.message)
        }

        return bmp
    }

    fun saveImageToGallery(pathName: String, fileName: String, bitmap: Bitmap, quality: Int) {
        if (bitmap == null) {
            //println("Cannot save image, cannot save null")
            return
        }

        //println("saving to gallery...")

        try {
            val pathname = "${Environment.getExternalStorageDirectory()}/$pathName"
            val myDir = File(pathname)

            if (!myDir.exists()) {
                myDir.mkdirs()
            }

            val file = File(myDir, fileName)
            if (file.exists()) file.delete()
            val out = FileOutputStream(file)
            //println("Aquired file handle")

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            //println("writing bitmap to file...")
            out.flush()
            out.close()
            //println("image written")
        } catch (e: Exception) {
            //println("Error in writing bitmap to file: " + e.message)
            Logging.createLogEntry(Logging.loggingLevel_debug, loggingModuleID, "Cannot save image.")
            Log.d("Exception", e.message)
        }
    }


    fun close() {
        /**Thread(Runnable {
        if(SQLDB != null){
        if(SQLDB!!.isOpen){
        SQLDB!!.close()
        }
        SQLDB = null
        }
        }).start()*/
    }


}