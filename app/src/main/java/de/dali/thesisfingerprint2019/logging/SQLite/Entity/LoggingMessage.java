package de.dali.thesisfingerprint2019.logging.SQLite.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


/**
 * @Entity(foreignKeys = @ForeignKey(entity = Module.class,
 * parentColumns = "moduleID",
 * childColumns = "moduleID",
 * onDelete = ForeignKey.CASCADE))
 */


@Entity(tableName = "loggingMessage",
        foreignKeys = {
                @ForeignKey(entity = Phone.class,
                        parentColumns = "phoneID",
                        childColumns = "phoneID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Run.class,
                        parentColumns = "runID",
                        childColumns = "runID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Module.class,
                        parentColumns = "moduleID",
                        childColumns = "moduleID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Image.class,
                        parentColumns = "imageID",
                        childColumns = "imageID",
                        onDelete = ForeignKey.CASCADE)
        })
public class LoggingMessage {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long messageID;

    public long getMessageID() {
        return messageID;
    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }

    @NonNull
    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(@NonNull String appVersion) {
        this.appVersion = appVersion;
    }

    public long getPhoneID() {
        return phoneID;
    }

    public void setPhoneID(long phoneID) {
        this.phoneID = phoneID;
    }

    public Long getRunID() {
        return runID;
    }

    public void setRunID(Long runID) {
        this.runID = runID;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public long getModuleID() {
        return moduleID;
    }

    public void setModuleID(long moduleID) {
        this.moduleID = moduleID;
    }

    @NonNull
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NonNull String timestamp) {
        this.timestamp = timestamp;
    }

    public void setMessage(@NonNull String message) {
        this.message = message;
    }

    @NonNull
    private String appVersion;

    @NonNull
    private long phoneID;

    private Long runID;

    @NonNull
    private long level;

    @NonNull
    private long moduleID;

    @NonNull
    private String timestamp;

    @NonNull
    private String message;

    //private Object data;

    public Long getImageID() {
        return imageID;
    }

    public void setImageID(Long imageID) {
        this.imageID = imageID;
    }

    private Long imageID;

    public String getMessage() {
        return message;
    }

    /*public LoggingMessage(@NonNull String appVersion, long phoneID, Long runID, long level, long moduleID, @NonNull String message, Object data) {
        this.appVersion = appVersion;
        this.phoneID = phoneID;
        this.runID = runID;
        this.level = level;
        this.moduleID = moduleID;
        this.message = message;
        this.data = data;

        timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
    }*/

    /*public LoggingMessage(@NonNull String appVersion, long phoneID, long level, long moduleID, @NonNull String message, Object data) {
        this.appVersion = appVersion;
        this.phoneID = phoneID;
        this.level = level;
        this.moduleID = moduleID;
        this.message = message;
        this.data = data;

        timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
    }*/

    /*public LoggingMessage(@NonNull String appVersion, long phoneID, Long runID, long level, long moduleID, @NonNull String message) {
        this.appVersion = appVersion;
        this.phoneID = phoneID;
        this.runID = runID;
        this.level = level;
        this.moduleID = moduleID;
        this.message = message;

        timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
    }*/

    public LoggingMessage(@NonNull String appVersion, long phoneID, long level, long moduleID, @NonNull String message, @NonNull String timestamp) {
        this.appVersion = appVersion;
        this.phoneID = phoneID;
        this.level = level;
        this.moduleID = moduleID;
        this.message = message;
        this.timestamp = timestamp;
        this.runID = null;
        this.imageID = null;
    }

    /*public LoggingMessage(@NonNull String appVersion, long phoneID, long level, long moduleID, @NonNull String message) {
        this.appVersion = appVersion;
        this.phoneID = phoneID;
        this.level = level;
        this.moduleID = moduleID;
        this.message = message;
        this.imageID = null;

        timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
    }*/
}
