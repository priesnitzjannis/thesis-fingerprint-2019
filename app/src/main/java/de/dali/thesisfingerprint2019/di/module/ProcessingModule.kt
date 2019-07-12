package de.dali.thesisfingerprint2019.di.module

import dagger.Module
import dagger.Provides
import de.dali.thesisfingerprint2019.processing.ProcessingThread
import de.dali.thesisfingerprint2019.processing.QualityAssuranceThread
import de.dali.thesisfingerprint2019.processing.common.Enhancement
import de.dali.thesisfingerprint2019.processing.common.RotateFinger
import de.dali.thesisfingerprint2019.processing.dali.*
import de.dali.thesisfingerprint2019.processing.stein.FingerDetectionImprecisely
import de.dali.thesisfingerprint2019.processing.stein.FingerDetectionPrecisely
import de.dali.thesisfingerprint2019.processing.stein.FingerSegmentation
import de.dali.thesisfingerprint2019.processing.stein.QualityAssurance
import javax.inject.Named
import javax.inject.Singleton

@Module
class ProcessingModule {

    //region QUALITY ASSURANCE STEIN
    @Provides
    @Named("qualityAssuranceStein")
    fun provideQualityAssurance(
        qualityAssurance: QualityAssurance
    ): QualityAssuranceThread = QualityAssuranceThread(
        qualityAssurance
    )

    @Provides
    @Singleton
    fun providesQualityAssurance(): QualityAssurance = QualityAssurance()
    //endregion

    //region PROCESSING STEIN
    @Provides
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
    fun providesFingerDetection(): FingerDetectionImprecisely = FingerDetectionImprecisely()

    @Provides
    @Singleton
    fun providesFingerDetectionPrec(): FingerDetectionPrecisely = FingerDetectionPrecisely()

    @Provides
    @Singleton
    fun providesFingerSegmentation(): FingerSegmentation = FingerSegmentation()

    //endregion

    //region QUALITY ASSURANCE DALI
    @Provides
    @Named("qualityAssuranceDali")
    fun provideNewQualityAssurance(
        multiFingerDetection: MultiFingerDetection,
        fingerRotationImprecise: FingerRotationImprecise,
        fingerBorderDetection: FingerBorderDetection,
        multiQualityAssurance: MultiQualityAssurance
    ): QualityAssuranceThread = QualityAssuranceThread(
        multiFingerDetection,
        fingerRotationImprecise,
        fingerBorderDetection,
        multiQualityAssurance
    )

    @Provides
    @Singleton
    fun provideFingerRotationImprecise(): FingerRotationImprecise = FingerRotationImprecise()

    @Provides
    @Singleton
    fun provideMultiQualityAssurance(): MultiQualityAssurance = MultiQualityAssurance()

    @Provides
    @Singleton
    fun provideFingerBorderDetection(): FingerBorderDetection = FingerBorderDetection()

    @Provides
    @Singleton
    fun provideMultiFingerDetection(): MultiFingerDetection = MultiFingerDetection()

    //endregion

    //region PROCESSING DALI
    @Provides
    @Named("pipelineDali")
    fun provideNewProcessing(
        rotateFinger: RotateFinger,
        findFingerTip: FindFingerTip,
        enhancement: Enhancement
    ): ProcessingThread = ProcessingThread(
        rotateFinger,
        findFingerTip,
        enhancement
    )

    //endregion

    //region COMMON
    @Provides
    @Singleton
    fun providesRotateFinger(): RotateFinger = RotateFinger()

    @Provides
    @Singleton
    fun providesEnhancement(): Enhancement = Enhancement()
    //endregion

}