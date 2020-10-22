package de.dali.demonstrator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.dali.demonstrator.data.local.converter.Converters
import de.dali.demonstrator.data.local.dao.FingerPrintDao
import de.dali.demonstrator.data.local.dao.ImageDao
import de.dali.demonstrator.data.local.dao.TestPersonDao
import de.dali.demonstrator.data.local.entity.FingerPrintEntity
import de.dali.demonstrator.data.local.entity.ImageEntity
import de.dali.demonstrator.data.local.entity.TestPersonEntity

@Database(
    entities = [FingerPrintEntity::class,
        TestPersonEntity::class,
        ImageEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    Converters::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fingerPrintDao(): FingerPrintDao
    abstract fun testPersonDao(): TestPersonDao
    abstract fun imageDao(): ImageDao
}
