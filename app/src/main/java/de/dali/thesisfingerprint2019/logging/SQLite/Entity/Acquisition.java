package de.dali.thesisfingerprint2019.logging.SQLite.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "acquisition")
public class Acquisition {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long acquisitionID;

    @NonNull
    private String start;

    private String end;

    @NonNull
    private boolean completed;

    //private Boolean possiblyBroken;

    @NonNull
    private String fingers;

    @NonNull
    private String location;

    @NonNull
    private double illumination;

    public Acquisition(@NonNull String location, double illumination, @NonNull String fingers) {
        this.start = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
        this.completed = false;
        this.location = location;
        this.illumination = illumination;
        this.fingers = fingers;
        this.end = null;
    }

    public long getAcquisitionID() {
        return acquisitionID;
    }

    public void setAcquisitionID(long acquisitionID) {
        this.acquisitionID = acquisitionID;
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

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed_) {
        completed = completed;
    }

    public void setFingers(@NonNull String fingers_){
        fingers = fingers_;
    }

    public String getFingers(){
        return fingers;
    }

    public void setLocation(@NonNull String location_){
        location = location_;
    }

    public String getLocation(){
        return location;
    }

    public void setIllumination(double illumination_){
        illumination = illumination_;
    }

    public double getIllumination(){
        return illumination;
    }

    public void cancel() {
        end = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
        completed = false;
    }

    public void complete(){
        end = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
        completed = true;
    }
}
