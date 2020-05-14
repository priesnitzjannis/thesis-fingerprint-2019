package de.dali.thesisfingerprint2019.logging

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import de.dali.thesisfingerprint2019.logging.SQLite.Dao.*
import de.dali.thesisfingerprint2019.logging.SQLite.LoggingDatabase
import javax.inject.Singleton

@Module
class LoggingDatabaseModule {

    @Provides
    @Singleton
    internal fun provideDatabase(application: Application): LoggingDatabase {
        return Room.databaseBuilder(
            application, LoggingDatabase::class.java, "fingerprint-database.db"
        )
            .allowMainThreadQueries()
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }

    @Provides
    @Singleton
    internal fun provideImageDao(LoggingDatabase: LoggingDatabase): ImageDao {
        return LoggingDatabase.imageDao()
    }

    @Provides
    @Singleton
    internal fun provideLoggingMessageDao(LoggingDatabase: LoggingDatabase): LoggingMessageDao {
        return LoggingDatabase.loggingMessageDao()
    }

    @Provides
    @Singleton
    internal fun provideModuleDao(LoggingDatabase: LoggingDatabase): ModuleDao {
        return LoggingDatabase.moduleDao()
    }

    /**@Provides
    @Singleton
    internal fun providePersonDao(LoggingDatabase: LoggingDatabase): PersonDao {
        return LoggingDatabase.personDao()
    }*/

    @Provides
    @Singleton
    internal fun providePhoneDao(LoggingDatabase: LoggingDatabase): PhoneDao {
        return LoggingDatabase.phoneDao()
    }

    @Provides
    @Singleton
    internal fun provideRunDao(LoggingDatabase: LoggingDatabase): RunDao {
        return LoggingDatabase.runDao()
    }

}
