package de.dali.thesisfingerprint2019.data.local.dao

import androidx.room.*
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import io.reactivex.Single

@Dao
interface FingerPrintDao {

    @Insert
    fun insert(fingerprint: FingerPrintEntity)

    @Update
    fun update(fingerprint: FingerPrintEntity)

    @Delete
    fun delete(fingerprint: FingerPrintEntity)

    @Query("DELETE FROM finger_print")
    fun deleteAll()

    @Query("SELECT * from finger_print")
    fun getAllFingerprints(): Single<List<FingerPrintEntity>>

}