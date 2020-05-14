package de.dali.thesisfingerprint2019.logging.SQLite.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "run")
public class Run {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long runID;

    @NonNull
    private String start;

    private String end;

    private int returnCode;


    public Run() {
        start = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
    }


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


    public void end(int _returnCode) {
        end = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
        returnCode = _returnCode;
    }
}
