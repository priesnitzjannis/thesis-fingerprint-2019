package de.dali.thesisfingerprint2019.logging.SQLite.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.dali.thesisfingerprint2019.logging.SQLite.Entity.Acquisition;

@Dao
public interface AcquisitionDao {
    @Query("Select * from acquisition")
    List<Acquisition> getAll();

    @Query("Delete from acquisition")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAcquisition(Acquisition acquisition);

    @Update
    void updateAcquisition(Acquisition acquisition);

    @Delete
    void deleteAcquisition(Acquisition acquisition);

    @Query("Select * from acquisition where acquisitionID = :acquisitionID")
    List<Acquisition> getAcquisitionByID(long acquisitionID);
}
