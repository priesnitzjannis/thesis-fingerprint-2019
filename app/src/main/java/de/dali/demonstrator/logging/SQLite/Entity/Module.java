package de.dali.demonstrator.logging.SQLite.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "module")
public class Module {

    @PrimaryKey()
    @NonNull
    private long moduleID;

    @NonNull
    private String name;

    public long getModuleID() {
        return moduleID;
    }

    public void setModuleID(long moduleID) {
        this.moduleID = moduleID;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public Module(long moduleID, @NonNull String name) {
        this.moduleID = moduleID;
        this.name = name;
    }
}
