package de.dali.thesisfingerprint2019.di.module

import android.app.Application
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.room.Update
import dagger.Module
import dagger.Provides
import de.dali.thesisfingerprint2019.data.repository.FingerPrintRepository
import de.dali.thesisfingerprint2019.data.repository.ImageRepository
import de.dali.thesisfingerprint2019.data.repository.TestPersonRepository
import de.dali.thesisfingerprint2019.utils.UpdateDB
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    internal fun provideSensorManager(application: Application): SensorManager =
        application.getSystemService(SENSOR_SERVICE) as SensorManager

    @Provides
    @Singleton
    internal fun provideSensor(sensorManager: SensorManager): Sensor =
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    @Provides
    @Singleton
    internal fun provideUpdateDB(userRepo : TestPersonRepository,
                                 fingerPersonRepository: FingerPrintRepository,
                                 imageRepository: ImageRepository): UpdateDB = UpdateDB(userRepo, fingerPersonRepository,imageRepository)

}
