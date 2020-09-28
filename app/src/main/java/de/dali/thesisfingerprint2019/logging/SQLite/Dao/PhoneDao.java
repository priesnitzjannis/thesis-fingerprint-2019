package de.dali.thesisfingerprint2019.logging.SQLite.Dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.dali.thesisfingerprint2019.logging.SQLite.Entity.Phone;
//import io.reactivex.Single;

@Dao
public interface PhoneDao {

    @Query("Select * from phone")
    List<Phone> getAll();

    @Query("Delete from phone")
    void deleteAll();

    @Query("Select * from phone where model = :model")
    List<Phone> getPhoneByModel(String model);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPhone(Phone phone);

    @Update
    void updatePhone(Phone phone);

    @Delete
    void deletePhone(Phone phone);
}
