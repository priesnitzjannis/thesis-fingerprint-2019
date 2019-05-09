package de.dali.thesisfingerprint2019.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.dali.thesisfingerprint2019.data.local.dao.FingerPrintDao
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity

@Database(
    entities = [FingerPrintEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters()
abstract class AppDatabase : RoomDatabase() {

    abstract fun fingerPrintDao(): FingerPrintDao

}
