package de.dali.demonstrator.logging.SQLite.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image")
public class Image {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long imageID;

    @NonNull
    private String fileExtension;

    @NonNull
    private String timestamp;

    @NonNull
    private int width;

    @NonNull
    private int height;

    public Image(int height, int width, String fileExtension, String timestamp) {
        this.height = height;
        this.width = width;
        this.fileExtension = fileExtension;
        this.timestamp = timestamp;
    }

    public long getImageID() {
        return imageID;
    }

    public void setImageID(long imageID) {
        this.imageID = imageID;
    }

    @NonNull
    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(@NonNull String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFilename() {
        return "LoggingImage_" + imageID + fileExtension;
    }

    @NonNull
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NonNull String timestamp) {
        this.timestamp = timestamp;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}