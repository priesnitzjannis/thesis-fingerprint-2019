package de.dali.thesisfingerprint2019.di.module

import dagger.Module
import dagger.Provides
import de.dali.thesisfingerprint2019.processing.ProcessingPipeline
import de.dali.thesisfingerprint2019.processing.stein.*
import de.dali.thesisfingerprint2019.ui.main.handler.UIHandler
import javax.inject.Named
import javax.inject.Singleton

@Module
class ProcessingModule {

    @Provides
    @Singleton
    fun provideProcessingPipeline(
        uiHandler: UIHandler,
        qualityAssurance: QualityAssurance,
        fingerDetection: FingerDetection,
        fingerSegmentation: FingerSegmentation,
        rotateFinger: RotateFinger,
        enhancement: Enhancement
    ): ProcessingPipeline {
        return ProcessingPipeline(
            uiHandler,
            qualityAssurance /*,
            fingerDetection,
            fingerSegmentation,
            rotateFinger,
            enhancement */
        )
    }

    @Provides
    @Singleton
    fun provideUiHandler(): UIHandler = UIHandler()

    @Provides
    @Singleton
    fun providesQualityAssurance(): QualityAssurance = QualityAssurance()

    @Provides
    @Singleton
    fun providesFingerDetection(): FingerDetection = FingerDetection()

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