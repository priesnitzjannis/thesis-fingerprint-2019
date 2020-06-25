package de.dali.thesisfingerprint2019.logging.SQLite.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "imageRun",
        foreignKeys = {
                @ForeignKey(entity = Acquisition.class,
                        parentColumns = "acquisitionID",
                        childColumns = "acquisitionID",
                        onDelete = ForeignKey.CASCADE)
        })
public class ImageRun {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long runID;

    @NonNull
    private String start;

    private String end;

    private int returnCode;

    @NonNull
    private long acquisitionID;


    public long getRunID() {
        return runID;
    }

    public void setRunID(long runID) {
        this.runID = runID;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    @NonNull
    public String getStart() {
        return start;
    }

    public void setStart(@NonNull String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public long getAcquisitionID() {
        return acquisitionID;
    }

    public void setAcquisitionID(long acquisitionID_) {
        acquisitionID = acquisitionID_;
    }

    public void end(int _returnCode) {
        end = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
        returnCode = _returnCode;
    }

    public ImageRun(@NonNull long acquisitionID) {
        this.start = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
        this.acquisitionID = acquisitionID;
    }

}
