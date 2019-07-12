package de.dali.thesisfingerprint2019.data.local.entity

import org.opencv.core.Mat

data class FingerPrintIntermediateEntity(
    val mat: Mat,
    val edgeDens: Double,
    val correctionDegreeImprecise: Double
)