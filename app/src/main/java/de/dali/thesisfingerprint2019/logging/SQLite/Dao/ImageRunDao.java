package de.dali.thesisfingerprint2019.logging.SQLite.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.dali.thesisfingerprint2019.logging.SQLite.Entity.ImageRun;

@Dao
public interface ImageRunDao {

    @Query("Select * from ImageRun")
    List<ImageRun> getAll();

    @Query("Delete from ImageRun")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRun(ImageRun imageRun);

    @Update
    void updateRun(ImageRun imageRun);

    @Delete
    void deleteRun(ImageRun imageRun);

    @Query("Select * from ImageRun where runID = :runID")
    List<ImageRun> getRunByID(long runID);
}
