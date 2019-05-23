package de.dali.thesisfingerprint2019.processing

import org.opencv.core.CvType.CV_16S

object Config {
    //region QA
    const val CENTER_SIZE_X = 150.0
    const val CENTER_SIZE_Y = 150.0
    const val CENTER_OFFSET_X = CENTER_SIZE_X / 2.0
    const val CENTER_OFFSET_Y = CENTER_SIZE_Y / 2.0
    const val GRAD_X = 0.75
    const val GRAD_Y = 0.75
    const val K_SIZE_GAUS = 3.0
    const val K_SIZE_SOBEL = 3
    const val SCALE = 1.0
    const val DELTA = 3.0
    const val DDEPTH = CV_16S
    const val EDGE_DENS_TRESHOLD = 15.0
    //endregion

    //region FD
    const val STEP_SIZE = 24
    //endregion

    //region ALL
    const val TRESHOLD_RED = 100.0
    //endregion

    const val MAX_KERNEL_LENGTH = 31
}