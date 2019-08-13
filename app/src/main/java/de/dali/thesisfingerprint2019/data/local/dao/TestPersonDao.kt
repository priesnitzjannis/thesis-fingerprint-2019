package de.dali.thesisfingerprint2019.data.local.dao

import androidx.room.*
import de.dali.thesisfingerprint2019.data.local.entity.TestPersonEntity
import io.reactivex.Single

@Dao
interface TestPersonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fingerprint: TestPersonEntity): Long

    @Update
    fun update(fingerprint: TestPersonEntity)

    @Delete
    fun delete(fingerprint: TestPersonEntity)

    @Query("DELETE FROM test_person")
    fun deleteAll()

    @Query("SELECT * FROM test_person")
    fun getAllTestPerson(): List<TestPersonEntity>

}