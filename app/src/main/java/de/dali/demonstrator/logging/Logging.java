package de.dali.demonstrator.logging;

import android.content.Context;
import android.os.Build;

import org.opencv.core.Mat;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.dali.demonstrator.logging.SQLite.Entity.Module;

import static android.os.SystemClock.elapsedRealtimeNanos;

/**
 * The main logic for the logging mechanism
 */
public final class Logging {
    private static long loggingLevel = 0;
    private static Long run = null;
    private static Long acquisition = null;
    private static loggerValues logger = loggerValues.logConsole;
    private static boolean initialised = false;
    private static boolean imageLoggingEnabled = true;
    private static long loggingModuleID;

    private static LogSQLite SQLDB = null;

    public static final long loggingLevel_param = 1;
    public static final long loggingLevel_critical = 2;
    public static final long loggingLevel_medium = 3;
    public static final long loggingLevel_debug = 4;

    private static final Map<Long, Boolean> paramsWritten = new HashMap<Long, Boolean>();


    private static long imageRunStartTime = 0;
    private static long acquisitionStartTime = 0;
    /**
     * All methods are static, therefore a callable constructor does not make sense
     */
    private Logging() {
    }

    /**
     * Set the location for logs to be saved
     *
     * @param logger the logation as enum
     */
    public static void setLogger(loggerValues logger) {
        Logging.logger = logger;
    }

    public static void enableImageLogging() {
        imageLoggingEnabled = true;
    }

    public static void disableImageLogging() {
        imageLoggingEnabled = false;
    }

    /**
     * Get the logging level
     *
     * @return the logging level
     */
    public static long getLoggingLevel() {
        return loggingLevel;
    }

    /**
     * Set the logging level
     *
     * @param newLoggingLevel the desired logging level
     */
    private static void setLoggingLevel(long newLoggingLevel) {
        loggingLevel = newLoggingLevel;
        return;
    }

    /**
     * Set the logging level
     *
     * @param newLoggingLevel the desired logging level
     * @param moduleID        The ID of the module that changed the logging level
     */
    public static void setLoggingLevel(long newLoggingLevel, long moduleID) {
        setLoggingLevel(newLoggingLevel);
        createLogEntry(0, moduleID, "Logging level set to " + newLoggingLevel + " by module #" + moduleID);
        return;
    }

    /**
     * Set the logging level
     *
     * @param newLoggingLevel the desired logging level
     * @param location        The location from where the logging level has been changed, in textual form
     */
    public static void setLoggingLevel(long newLoggingLevel, String location) {
        setLoggingLevel(newLoggingLevel);
        createLogEntry(0, 0, "Logging level set to " + newLoggingLevel + " by " + location);
        return;
    }

    /**
     * Start a new run.
     * <p>
     * What qualifies as a run run can be different for every app and is not mandatory.
     * Meant to describe recurring processes in apps where these are the main purpose.
     */
    public static void startRun() {
        if (run != null) {
            createLogEntry(Logging.loggingLevel_critical, loggingModuleID, "Attempt to start an image run while one is already running");
            return;
        }

        switch (logger) {
            case logSQLite: {
                run = SQLDB.startRun(acquisition);
                break;
            }
            case logConsole:
            case logFile:
            case logMySQL: {
                break;
            }
            default: {
                // all possible cases for logging methods should be covered by the switch statement, i.e. the default case should never be used
                return;
            }
        }

        imageRunStartTime = System.currentTimeMillis();
        createLogEntry(Logging.loggingLevel_critical, loggingModuleID, "ImageRun " + run + " has begun" + getISOTimestamp());
        return;
    }

    /**
     * End the current run.
     *
     * @param code The return code of the current run
     */
    public static void endRun(int code) {
        if (run == null) {
            createLogEntry(Logging.loggingLevel_critical, loggingModuleID, "Attempt to end a non existent image run");
            return;
        }

        createLogEntry(Logging.loggingLevel_critical, loggingModuleID, "ImageRun " + run + " has ended with return code " + code + "in " +  (System.currentTimeMillis() - imageRunStartTime));

        switch (logger) {
            case logSQLite: {
                SQLDB.endRun(run, code);
                break;
            }
            case logConsole:
            case logFile:
            case logMySQL: {
                break;
            }
            default: {
                // all possible cases for logging methods should be covered by the switch statement, i.e. the default case should never be used
                return;
            }
        }

        run = null;
    }

    public static void startAcquisition(String location, double illumination, String fingers) {
        if (acquisition != null) {
            //createLogEntry(Logging.loggingLevel_critical, loggingModuleID, "Attempt to start an acquisition while one is already running");
            return;
        }

        switch (logger) {
            case logSQLite: {
                acquisition = SQLDB.startAcquisition(location, illumination, fingers);
                break;
            }
            case logConsole:
            case logFile:
            case logMySQL: {
                break;
            }
            default: {
                // all possible cases for logging methods should be covered by the switch statement, i.e. the default case should never be used
                return;
            }
        }

        acquisitionStartTime = System.currentTimeMillis();
        createLogEntry(Logging.loggingLevel_critical, loggingModuleID, "Acquisition " + acquisition + " has begun" +  getISOTimestamp());
    }

    public static void cancelAcquisition() {
        if (acquisition == null) {
            //createLogEntry(Logging.loggingLevel_critical, loggingModuleID, "Attempt to cancel a non existent acquisition");
            return;
        }

        createLogEntry(Logging.loggingLevel_critical, loggingModuleID, "Acquisition " + acquisition + " has been cancelled in "  + (System.currentTimeMillis() - imageRunStartTime));

        switch (logger) {
            case logSQLite: {
                SQLDB.cancelAcquisition(acquisition);
                break;
            }
            case logConsole:
            case logFile:
            case logMySQL: {
                break;
            }
            default: {
                // all possible cases for logging methods should be covered by the switch statement, i.e. the default case should never be used
                return;
            }
        }

        acquisition = null;
    }

    public static void completeAcquisition(long fingerPrintID) {
        if (acquisition == null) {
            //createLogEntry(Logging.loggingLevel_critical, loggingModuleID, "Attempt to complete a non existent acquisition");
            return;
        }


        createLogEntry(Logging.loggingLevel_critical, loggingModuleID, "Acquisition " + acquisition + " has been completed in " + (System.currentTimeMillis() - acquisitionStartTime));

        switch (logger) {
            case logSQLite: {
                SQLDB.completeAcquisition(acquisition, fingerPrintID);
                break;
            }
            case logConsole:
            case logFile:
            case logMySQL: {
                break;
            }
            default: {
                // all possible cases for logging methods should be covered by the switch statement, i.e. the default case should never be used
                return;
            }
        }
        acquisition = null;
    }


    /**
     * Get a timestamp of the current time in UTC±00:00
     *
     * @return The current timestamp in UTC±00:00
     */
    private static String getISOTimestamp() {
        // Z stands for UTC±00:00
        // to do:
        // Check if Z can be safely replaced by UTC±00:00
        ZonedDateTime zdt = ZonedDateTime.now();
        return zdt.format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * Get the current phone model.
     *
     * @return The current phone model
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * Initialise the modules associated with the app
     */
    private static void initModules(ArrayList<Module> modules) {
        switch (logger) {
            case logSQLite: {
                for (Module currentModule : modules) {
                    SQLDB.addModule(currentModule);
                    if (currentModule.getName().equals("Logging Internal")) {
                        loggingModuleID = currentModule.getModuleID();
                    }
                    paramsWritten.put(currentModule.getModuleID(), false);
                }
                break;
            }
            case logConsole:
            case logFile:
            case logMySQL: {
                break;
            }
            default: {
                // all possible cases for logging methods should be covered by the switch statement, i.e. the default case should never be used
            }
        }
    }

    /**
     * Initialise the logging mechanism.
     *
     * @param loggingLocation What logging location to use, as enum
     * @param newLoggingLevel The logging level to use
     * @param newAppVersion   The current app version
     * @param context
     * @return
     */
    public static short init(loggerValues loggingLocation, long newLoggingLevel, String newAppVersion, ArrayList<Module> modules, Context context) {
        setLoggingLevel(newLoggingLevel);
        setLogger(loggingLocation);
        switch (logger) {
            case logConsole: {
                initialised = true;
                break;
            }
            case logSQLite: {
                SQLDB = new LogSQLite();
                SQLDB.init(newAppVersion, loggingModuleID, context);

                initModules(modules);

                initialised = true;
                break;
            }
            case logFile:
            case logMySQL: {
                initialised = false;
                break;
            }
            default: {
                // all possible cases for logging methods should be covered by the switch statement, i.e. the default case should never be used
                initialised = false;
            }
        }

        if (initialised) {
            // to do:
            // redo createLogEntry signature
            createLogEntry(Logging.loggingLevel_critical, loggingModuleID, "Logging initialised, logging level: " + newLoggingLevel + "; App version: " + newAppVersion);
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Function used to measure the execution time of saving a logging message.
     * Simply swap the function name of this one with createLogEntry()
     *
     * @param loggingLevel The logging level
     * @param moduleid     The module creating the logging message
     * @param message      The logging message
     * @return true if the message has been logged, false otherwise
     */
    public static boolean createLogEntryTimeMeasurement(long loggingLevel, long moduleid, String message) {
        long start = elapsedRealtimeNanos();
        boolean returnVal = createLogEntryTimeMeasurement(loggingLevel, moduleid, message);
        long duration = elapsedRealtimeNanos() - start;
        println("Creating text based logging message took " + duration + "ns.");
        createLogEntryTimeMeasurement(loggingLevel_critical,10,"Creating text based logging message took " + duration + "ns.");
        return returnVal;
    }

    /**
     * Function used to measure the execution time of saving a logging message
     * Simply swap the function name of this one with createLogEntry()
     *
     * @param loggingLevel The logging level
     * @param moduleID     The module id
     * @param message      The logging message
     * @param origMat      The image to be saved
     * @return true if the message has been logged, false otherwise
     */
    public static boolean createLogEntryTimeMeasurement(long loggingLevel, long moduleID, String message, Mat origMat) {
        long start = elapsedRealtimeNanos();
        boolean returnVal = createLogEntryTimeMeasurement(loggingLevel, moduleID, message, origMat);
        long duration = elapsedRealtimeNanos() - start;
        createLogEntryTimeMeasurement(loggingLevel_critical,10,"Creating image based logging message took " + duration + "ns.");
        return returnVal;
    }

    /**
     * Create a log entry.
     *
     * @param loggingLevel The logging level
     * @param moduleid     The module creating the logging message
     * @param message      The logging message
     * @return true if the message has been logged, false otherwise
     */
    public static boolean createLogEntry(long loggingLevel, long moduleid, String message) {
        // to do:
        // implement possible logging locations
        // implement all items that need to be logged
        // error code as return values
        // Rethink passing of each variable

        if (loggingLevel > loggingLevel_debug) {
            System.out.println("invalid logging level");
            return false;
        }

        // Check if the logging module has been initialised and if the logging level is low enough
        if (loggingLevel > Logging.loggingLevel || !initialised) {
            return false;
        } else if (loggingLevel == loggingLevel_param) {
            if (paramsWritten.get(moduleid)) {
                return false;
            } else {
                paramsWritten.put(moduleid, true);
            }
        }

        boolean result = false;

        switch (logger) {
            case logConsole: {
                result = LogConsole.createLogEntry(0, "fix this", getPhoneModel(), loggingLevel, moduleid, getISOTimestamp(), message);
                break;
            }
            case logSQLite: {
                if (run == null) {
                    result = SQLDB.createLogEntry(loggingLevel, moduleid, message, getISOTimestamp());
                } else {
                    result = SQLDB.createLogEntry(loggingLevel, moduleid, message, getISOTimestamp(), run);
                }
                //result = SQLDB.createLogEntry(0, "fix this", getPhoneModel(), loggingLevel, moduleid, getISOTimestamp(), message);
                break;
            }
            case logMySQL:
            case logFile: {
                break;
            }
            default: {
                // all possible cases for logging methods should be covered by the switch statement, i.e. the default case should never be used
                return false;
            }
        }

        return result;
    }

    /**
     * Create a log entry with an attached image.
     * The image will be saved to the gallery.
     *
     * @param loggingLevel The logging level
     * @param moduleID     The module id
     * @param message      The logging message
     * @param origMat      The image to be saved
     * @return true if the message has been logged, false otherwise
     */
    public static boolean createLogEntry(long loggingLevel, long moduleID, String message, Mat origMat) {
        // to do:
        // error code as return values

        if (loggingLevel > loggingLevel_debug) {
            System.out.println("invalid logging level");
            return true;
        }

        // Check if the logging module has been initialised and if the logging level is low enough
        if (loggingLevel > Logging.loggingLevel || !initialised) {
            return true;
        }

        Mat imageMat = origMat.clone();
        boolean result = false;

        switch (logger) {
            case logConsole: {
                result = LogConsole.createLogEntry(0, "fix this", getPhoneModel(), loggingLevel, moduleID, getISOTimestamp(), message, imageMat);
                break;
            }
            case logSQLite: {
                if (imageLoggingEnabled) {
                    if (run == null) {
                        result = SQLDB.createLogEntry(loggingLevel, moduleID, message, getISOTimestamp(), imageMat);
                    } else {
                        result = SQLDB.createLogEntry(loggingLevel, moduleID, message, getISOTimestamp(), run, imageMat);
                    }
                } else {
                    if (run == null) {
                        result = SQLDB.createLogEntry(loggingLevel, moduleID, message, getISOTimestamp());
                    } else {
                        result = SQLDB.createLogEntry(loggingLevel, moduleID, message, getISOTimestamp(), run);
                    }
                }
                break;
            }
            case logMySQL:
            case logFile: {
                break;
            }
            default: {
                // all possible cases for logging methods should be covered by the switch statement, i.e. the default case should never be used
                return true;
            }
        }

        return !result;
    }

    /*
    public static void close(){
    // Currently there is no action necessary when logging is ended

        switch (logger) {
            case logSQLite: {
                SQLDB.close();
                break;
            }
            case logConsole:
            case logMySQL:
            case logFile: {
                break;
            }
            default: {
                // all possible cases for logging methods should be covered by the switch statement, i.e. the default case should never be used
                return;
            }
        }
        return;
    }*/

    /**
     * All possible logging locations
     */
    public enum loggerValues {
        logConsole,
        logFile,
        logMySQL,
        logSQLite
    }

    // -------------------------------------------------------------------------------------------

    public static void println(String message) {
        System.out.println("Logging: " + message);
    }
}
