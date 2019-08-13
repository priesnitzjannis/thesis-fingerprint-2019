package de.dali.thesisfingerprint2019.data.local.dao

import androidx.room.*
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import io.reactivex.Single

@Dao
interface FingerPrintDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fingerprint: FingerPrintEntity): Long

    @Update
    fun update(fingerprint: FingerPrintEntity)

    @Delete
    fun delete(fingerprint: FingerPrintEntity)

    @Query("DELETE FROM finger_print")
    fun deleteAll()

    @Query("SELECT * from finger_print")
    fun getAllFingerprints(): List<FingerPrintEntity>

    @Query("SELECT * from finger_print WHERE personID = :id")
    fun getAllFingerprintsByTestPerson(id: Long): Single<List<FingerPrintEntity>>

}