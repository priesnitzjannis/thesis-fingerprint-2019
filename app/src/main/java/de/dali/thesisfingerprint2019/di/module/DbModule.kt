package de.dali.thesisfingerprint2019.di.module

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import de.dali.thesisfingerprint2019.data.local.AppDatabase
import de.dali.thesisfingerprint2019.data.local.dao.FingerPrintDao
import de.dali.thesisfingerprint2019.data.local.dao.ImageDao
import de.dali.thesisfingerprint2019.data.local.dao.TestPersonDao
import javax.inject.Singleton

@Module
class DbModule {

    @Provides
    @Singleton
    internal fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application, AppDatabase::class.java, "fingerprint-database.db"
        )
            .allowMainThreadQueries()
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }

    @Provides
    @Singleton
    internal fun provideFingerPrintDao(appDatabase: AppDatabase): FingerPrintDao {
        return appDatabase.fingerPrintDao()
    }

    @Provides
    @Singleton
    internal fun provideTestPersonDao(appDatabase: AppDatabase): TestPersonDao {
        return appDatabase.testPersonDao()
    }

    @Provides
    @Singleton
    internal fun provideImageDao(appDatabase: AppDatabase): ImageDao {
        return appDatabase.imageDao()
    }

}
