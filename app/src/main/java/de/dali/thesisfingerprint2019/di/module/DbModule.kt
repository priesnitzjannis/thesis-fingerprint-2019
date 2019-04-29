package de.dali.thesisfingerprint2019.di.module

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import de.dali.thesisfingerprint2019.data.local.AppDatabase
import de.dali.thesisfingerprint2019.data.local.dao.FingerPrintDao
import javax.inject.Singleton

@Module
class DbModule {

    @Provides
    @Singleton
    internal fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application, AppDatabase::class.java, "fingerprint-database.db"
        )
            .allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    internal fun provideMovieDao(appDatabase: AppDatabase): FingerPrintDao {
        return appDatabase.movieDao()
    }

}
