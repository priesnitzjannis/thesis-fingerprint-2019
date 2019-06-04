package de.dali.thesisfingerprint2019.di.module

import dagger.Module
import dagger.Provides
import de.dali.thesisfingerprint2019.processing.ProcessingThread
import de.dali.thesisfingerprint2019.processing.QualityAssuranceThread
import de.dali.thesisfingerprint2019.processing.common.Enhancement
import de.dali.thesisfingerprint2019.processing.common.QualityAssurance
import de.dali.thesisfingerprint2019.processing.common.RotateFinger
import de.dali.thesisfingerprint2019.processing.dali.FingerBorderDetection
import de.dali.thesisfingerprint2019.processing.dali.FingerSeparation
import de.dali.thesisfingerprint2019.processing.dali.MultiFingerDetection
import de.dali.thesisfingerprint2019.processing.stein.FingerDetectionImprecisely
import de.dali.thesisfingerprint2019.processing.stein.FingerDetectionPrecisely
import de.dali.thesisfingerprint2019.processing.stein.FingerSegmentation
import de.dali.thesisfingerprint2019.ui.main.handler.UIHandler
import javax.inject.Named
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
    @Named("pipelineStein")
    fun provideProcessing(
        fingerDetection: FingerDetectionImprecisely,
        rotateFinger: RotateFinger,
        fingerDetectionPrec: FingerDetectionPrecisely,
        fingerSegmentation: FingerSegmentation,
        enhancement: Enhancement
    ): ProcessingThread = ProcessingThread(
        fingerDetection,
        rotateFinger,
        fingerDetectionPrec,
        fingerSegmentation,
        enhancement
    )

    @Provides
    @Singleton
    @Named("pipelineDali")
    fun provideNewPipeline(
        multiFingerDetection: MultiFingerDetection,
        fingerBorderDetection: FingerBorderDetection,
        fingerSeparation: FingerSeparation
    ): ProcessingThread = ProcessingThread(
        multiFingerDetection,
        fingerBorderDetection,
        fingerSeparation
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
    fun providesFingerDetectionPrec(): FingerDetectionPrecisely = FingerDetectionPrecisely()

    @Provides
    @Singleton
    fun providesFingerSegmentation(): FingerSegmentation = FingerSegmentation()

    @Provides
    @Singleton
    fun providesRotateFinger(): RotateFinger = RotateFinger()

    @Provides
    @Singleton
    fun providesEnhancement(): Enhancement = Enhancement()

    @Provides
    @Singleton
    fun provideFingerBorderDetection(): FingerBorderDetection = FingerBorderDetection()

    @Provides
    @Singleton
    fun provideFingerSeparation(): FingerSeparation = FingerSeparation()

    @Provides
    @Singleton
    fun provideMultiFingerDetection(): MultiFingerDetection = MultiFingerDetection()
}