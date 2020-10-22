package de.dali.demonstrator.data.local.entity

import org.opencv.core.Mat

data class FingerPrintIntermediateEntity(
    val mat: Mat,
    val edgeDens: Double,
    val imgSizeOk: Boolean,
    val correctionDegreeImprecise: Double
)