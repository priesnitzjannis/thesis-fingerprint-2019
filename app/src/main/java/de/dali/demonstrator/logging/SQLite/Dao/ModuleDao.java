package de.dali.demonstrator.logging.SQLite.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.dali.demonstrator.logging.SQLite.Entity.Module;

@Dao
public interface ModuleDao {

    @Query("Select * from module")
    List<Module> getAll();

    @Query("Delete from module")
    void deleteAll();

    @Query("Select * from module where name = :name")
    List<Module> getModuleByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertModule(Module module);

    @Update
    void updateModule(Module module);

    @Delete
    void deleteModule(Module module);
}
