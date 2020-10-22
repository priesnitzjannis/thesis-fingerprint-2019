package de.dali.demonstrator.logging.SQLite.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.dali.demonstrator.logging.SQLite.Entity.Image;

@Dao
public interface ImageDao {

    @Query("Select * from image")
    List<Image> getAll();

    @Query("Delete from image")
    void deleteAll();

    //@Query("Select * from image where filename = :name")
    //List<Image> getImageByName(String name);

    @Query("Select * from image where imageID = :ID")
    List<Image> getImageByID(long ID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertImage(Image image);

    @Update
    void updateImage(Image image);

    @Delete
    void deleteImage(Image image);
}
