package de.dali.thesisfingerprint2019.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.dali.thesisfingerprint2019.data.local.converter.Converters
import de.dali.thesisfingerprint2019.data.local.dao.FingerPrintDao
import de.dali.thesisfingerprint2019.data.local.dao.TestPersonDao
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.data.local.entity.TestPersonEntity

@Database(
    entities = [FingerPrintEntity::class,
        TestPersonEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    Converters::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fingerPrintDao(): FingerPrintDao
    abstract fun testPersonDao(): TestPersonDao

}
