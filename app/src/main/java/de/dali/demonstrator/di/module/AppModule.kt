package de.dali.demonstrator.di.module

import android.app.Application
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorManager
import dagger.Module
import dagger.Provides
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

}
