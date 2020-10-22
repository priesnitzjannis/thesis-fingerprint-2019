package de.dali.demonstrator.logging.SQLite.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "phone")
public class Phone {
    // Public class vs data class ??

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long phoneID;

    @NonNull
    private String model;

    private String name;


    public long getPhoneID() {
        return phoneID;
    }

    public String getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public void setPhoneID(long _phoneID) {
        phoneID = _phoneID;
    }

    public void setModel(String _model) {
        model = _model;
    }

    public void setName(String _name) {
        name = _name;
    }

    public Phone() {
    }

    public Phone(String _model) {
        model = _model;
        name = "This is a local database, the will only ever be one phone (hopefully)";
    }
}
