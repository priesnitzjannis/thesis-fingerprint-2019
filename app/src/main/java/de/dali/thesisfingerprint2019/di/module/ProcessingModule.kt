package de.dali.thesisfingerprint2019.di.module

import dagger.Module
import dagger.Provides
import de.dali.thesisfingerprint2019.processing.ProcessingThread
import de.dali.thesisfingerprint2019.processing.QualityAssuranceThread
import de.dali.thesisfingerprint2019.processing.stein.Enhancement
import de.dali.thesisfingerprint2019.processing.stein.FingerSegmentation
import de.dali.thesisfingerprint2019.processing.stein.QualityAssurance
import de.dali.thesisfingerprint2019.processing.stein.RotateFinger
import de.dali.thesisfingerprint2019.processing.stein.FingerDetectionImprecisely
import de.dali.thesisfingerprint2019.ui.main.handler.UIHandler
import javax.inject.Singleton

@Module
class ProcessingModule {

    @Provides
    @Singleton
    fun provideQualityAssurance(
        qualityAssurance: QualityAssurance
    ): QualityAssuranceThread = QualityAssuranceThread(
        qualityAssurance
    )

    @Provides
    @Singleton
    fun provideProcessing(
        fingerDetection: FingerDetectionImprecisely
        /*
        fingerSegmentation: FingerSegmentation,
        rotateFinger: RotateFinger,
        enhancement: Enhancement
        */
    ): ProcessingThread = ProcessingThread(
        fingerDetection
        /*
        fingerSegmentation,
        rotateFinger,
        enhancement
        */
    )


    @Provides
    @Singleton
    fun provideUiHandler(): UIHandler = UIHandler()

    @Provides
    @Singleton
    fun providesQualityAssurance(): QualityAssurance = QualityAssurance()

    @Provides
    @Singleton
    fun providesFingerDetection(): FingerDetectionImprecisely = FingerDetectionImprecisely()

    @Provides
    @Singleton
    fun providesFingerSegmentation(): FingerSegmentation = FingerSegmentation()

    @Provides
    @Singleton
    fun providesRotateFinger(): RotateFinger = RotateFinger()

    @Provides
    @Singleton
    fun providesEnhancement(): Enhancement = Enhancement()

}