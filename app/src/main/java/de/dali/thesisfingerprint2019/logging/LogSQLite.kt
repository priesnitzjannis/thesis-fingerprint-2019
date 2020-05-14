package de.dali.thesisfingerprint2019.logging

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.util.Log
import de.dali.thesisfingerprint2019.logging.SQLite.Entity.*
import de.dali.thesisfingerprint2019.logging.SQLite.LoggingDatabase
import org.opencv.core.CvException
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream

class LogSQLite {
    private var SQLDB: LoggingDatabase? = null
    var phone = Phone(Build.MODEL)
    var appVersion = "Placeholder"

    fun init(_appVersion: String, context: Context?) {
        SQLDB = LoggingDatabase.getInstance(context)
        appVersion = _appVersion

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

    fun insertModule(module: Module) {
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
            val img = Image(imageMat.height(), imageMat.width(), ".jpg", timestamp)

            img.imageID = SQLDB!!.imageDao().insertImage(img)

            saveImage("Logging_Module", img.filename, convertMatToBitMap(imageMat)!!, 100)

            loggingMessage.imageID = img.imageID


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

            val img = Image(imageMat.height(), imageMat.width(), ".jpg", timestamp)

            img.imageID = SQLDB!!.imageDao().insertImage(img)

            saveImage("Logging_Module", img.filename, convertMatToBitMap(imageMat)!!, 100)

            loggingMessage.imageID = img.imageID


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

    fun convertMatToBitMap(input: Mat): Bitmap? {
        var bmp: Bitmap? = null
        val rgb = Mat()

        Imgproc.cvtColor(input, rgb, Imgproc.COLOR_BGR2RGBA, 4)

        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888)

            org.opencv.android.Utils.matToBitmap(input, bmp, true)
        } catch (e: CvException) {
            Log.d("Exception", e.message)
        }

        return bmp
    }

    fun saveImage(pathName: String, fileName: String, bitmap: Bitmap, quality: Int) {
        val pathname = "${Environment.getExternalStorageDirectory()}/$pathName"
        val myDir = File(pathname)

        if (!myDir.exists()) myDir.mkdirs()

        val file = File(myDir, fileName)
        if (file.exists()) file.delete()
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        out.flush()
        out.close()
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