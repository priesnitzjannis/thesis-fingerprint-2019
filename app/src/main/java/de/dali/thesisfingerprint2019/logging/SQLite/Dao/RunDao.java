package de.dali.thesisfingerprint2019.logging.SQLite.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.dali.thesisfingerprint2019.logging.SQLite.Entity.Run;

@Dao
public interface RunDao {

    @Query("Select * from run")
    List<Run> getAll();

    @Query("Delete from run")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRun(Run run);

    @Update
    void updateRun(Run run);

    @Delete
    void deleteRun(Run run);

    @Query("Select * from run where runID = :runID")
    List<Run> getRunByID(long runID);
}
