package de.dali.demonstrator.data.local.dao

import androidx.room.*
import de.dali.demonstrator.data.local.entity.ImageEntity
import io.reactivex.Single

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(image: ImageEntity): Long

    @Update
    fun update(image: ImageEntity)

    @Delete
    fun delete(image: ImageEntity)

    @Query("DELETE FROM images")
    fun deleteAll()

    @Query("SELECT * FROM images")
    fun getAllImages(): Single<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE fingerPrintID = :id")
    fun getAllImagesByFingerprints(id: Long): Single<List<ImageEntity>>
}