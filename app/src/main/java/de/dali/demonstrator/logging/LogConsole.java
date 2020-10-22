package de.dali.demonstrator.logging;

import org.opencv.core.Mat;

public final class LogConsole {
    private LogConsole() {
    }

    public static int init() {
        return 0;
    }

    /*public static boolean createLogEntry(short loggingLevel, short moduleid, String message) {
        System.out.println("New log entry:\n\tlevel: " + loggingLevel + "\n\tmodule: " + moduleid + "\n\tmessage:\n\t\t" + message);
        return true;
    }

    public static boolean createLogEntry(short loggingLevel, short moduleid, String message, String model, String appVersion, String timestamp) {
        System.out.println("New log entry:\n\tlevel: " + loggingLevel + "\n\tmodule: " + moduleid + "\n\tmessage:\n\t\t" + message + "\n\tmodel: " + model + "\n\tappVersion: " + appVersion + "\n\ttimestamp: " + timestamp);
        return true;
    }*/

    public static boolean createLogEntry(long messageID, String appVersion, String phoneModel, long loggingLevel, long module, String timestamp, String message) {
        System.out.println("New log entry:\n\tmessage ID: " + messageID + "\n\tApp version: " + appVersion + "\n\tPhone model: " + phoneModel + "\n\tLogging level: " + loggingLevel + "\n\tModule: " + module + "\n\tTimestamp: " + timestamp + "\n\tMessage: " + message);
        return true;
    }

    public static boolean createLogEntry(long messageID, String appVersion, String phoneModel, long loggingLevel, long module, String timestamp, String message, Mat mat) {
        System.out.println("New log entry:\n\tmessage ID: " + messageID + "\n\tApp version: " + appVersion + "\n\tPhone model: " + phoneModel + "\n\tLogging level: " + loggingLevel + "\n\tModule: " + module + "\n\tTimestamp: " + timestamp + "\n\tMessage: " + message);
        return true;
    }
}
