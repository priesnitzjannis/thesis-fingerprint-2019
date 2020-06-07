package de.dali.thesisfingerprint2019.logging;

import android.content.Context;
import android.os.Build;

import org.opencv.core.Mat;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import de.dali.thesisfingerprint2019.logging.SQLite.Entity.Module;

/**
 * The main logic for the logging mechanism
 */
public final class Logging {
    private static long loggingLevel = 0;
    private static Long run = null;
    private static loggerValues logger = loggerValues.logConsole;
    private static boolean initialised = false;
    private static boolean imageLoggingEnabled = true;
    private static long loggingModuleID;

    private static LogSQLite SQLDB = null;

    public static long loggingLevel_critical = 20;
    public static long loggingLevel_some = 40;
    public static long loggingLevel_medium = 60;
    public static long loggingLevel_detailed = 80;
    public static long loggingLevel_debug = 100;

    /**
     * All methods are static, therefore a callable constructor does not make sense
     */
    private Logging() {
    }

    /**
     * Set the location for logs to be saved
     *
     * @param logger the logation as enum
     * @return true if successful, false otherwise
     */
    public static boolean setLogger(loggerValues logger) {
        Logging.logger = logger;
        return true;
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
        switch (logger) {
            case logSQLite: {
                run = SQLDB.startRun();
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

        createLogEntry(0, loggingModuleID, "Run " + run + " has begun");

        return;
    }

    /**
     * End the current run.
     *
     * @param code The return code of the current run
     */
    public static void endRun(int code) {
        if (run == null) {
            return;
        }

        createLogEntry(0, loggingModuleID, "Run " + run + " has ended with return code " + code);

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

    /**
     * Get a timestamp of the current time in UTC±00:00
     *
     * @return The current timestamp in UTC±00:00
     */
    private static String getISOTimestamp() {
        // Z stands for UTC±00:00
        // TODO
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

    /*
     * Initialise the logging mechanism.
     *
     * @param loggingLocation What logging location to use, as enum
     * @param newLoggingLevel The logging level to use
     * @param newAppVersion The current app version
     * @return 0 if successful, an error code otherwise
     */
    /*public static short init(loggerValues loggingLocation, short newLoggingLevel, String newAppVersion) {
        // TODO
        // Initialise database connection, filename and the likes for each logging method

        setLoggingLevel(newLoggingLevel);
        setAppVersion(newAppVersion);
        setLogger(loggingLocation);
        setphoneModel(Build.MODEL);
        switch (logger) {
            case logConsole: {
                LogConsole.init();
                initialised = true;
                break;
            }
            case logSQLite:
            case logFile:
            case logMySQL:
             {
                initialised = false;
                break;
            }
            default: {
                // all possible cases for logging methods should be covered by the switch statement, i.e. the default case should never be used
                initialised = false;
            }
        }

        if (initialised) {
            createLogEntry((short) 2,(short) loggingModuleID, "Logging initialised, logging level: " + newLoggingLevel + " App version: " + appVersion);
            return 0;
        } else {
            return -1;
        }
    }*/

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
                LogConsole.init();
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
            // TODO
            // redo createLogEntry signature
            createLogEntry((short) 0, (short) loggingModuleID, "Logging initialised, logging level: " + newLoggingLevel + " App version: " + newAppVersion);
            return 0;
        } else {
            return -1;
        }
    }

    /*
     * Initialise the logging mechanism for a database based logging.
     *
     * @param loggingLocation What logging location to use, as enum
     * @param newLoggingLevel The logging level to use
     * @param newAppVersion The current app version
     * @param databaseLocation The location where the database can be reached
     * @param databaseLogin The login for the database
     * @param databasePwd The password for the database
     * @return 0 if successful, an error code otherwise
     */
    /*public static short init(loggerValues loggingLocation, short newLoggingLevel, String newAppVersion,  String databaseLocation, String databaseLogin, String databasePwd) {
        // TODO
        // Implement Database connections
        return -1;
    }*/

    /**
     * Create a log entry.
     *
     * @param loggingLevel The logging level
     * @param moduleid     The module creating the logging message
     * @param message      The logging message
     * @return true if the message has been logged, false otherwise
     */
    public static boolean createLogEntry(long loggingLevel, long moduleid, String message) {
        // TODO
        // implement possible logging locations
        // implement all items that need to be logged
        // error code as return values
        // Rethink passing of each variable

        // Check if the logging module has been initialised and if the logging level is low enough
        if (loggingLevel > Logging.loggingLevel || !initialised) {
            return false;
        }

        boolean result = false;

        // TODO
        // Redo createLogEntry signature
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
        // TODO
        // implement possible logging locations
        // implement all items that need to be logged
        // error code as return values
        // Rethink passing of each variable

        // Check if the logging module has been initialised and if the logging level is low enough
        if (loggingLevel > Logging.loggingLevel || !initialised) {
            return false;
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
                return false;
            }
        }

        return result;
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
}
