package de.dali.thesisfingerprint2019.processing

import org.opencv.core.CvType.CV_16S
import org.opencv.core.Size

object Config {
    //region MultiFingerDetection
    const val MIN_AREA_SIZE = 30000
    //endregion

    //region FingerBorderDetection
    val DILATE_KERNEL_SIZE = Size(27.0, 27.0)
    const val DILATE_ITERATIONS = 2

    val ERODE_KERNEL_SIZE = Size(23.0, 23.0)
    const val ERODE_ITERATIONS = 2

    const val PIXEL_TO_CROP = 100

    val KERNEL_SIZE_BLUR = Size(7.0, 7.0)
    const val THRESHOLD_MAX = 120.0
    const val BLOCKSIZE = 29
    //endregion


    //region QA
    const val CENTER_SIZE_X = 50.0
    const val CENTER_SIZE_Y = 50.0
    const val CENTER_OFFSET_X = CENTER_SIZE_X / 2.0
    const val CENTER_OFFSET_Y = CENTER_SIZE_Y / 2.0
    const val GRAD_X = 0.75
    const val GRAD_Y = 0.75
    const val K_SIZE_SOBEL = 3
    const val SCALE = 1.0
    const val DELTA = 3.0
    const val DDEPTH = CV_16S
    const val EDGE_DENS_TRESHOLD = 10.0

    val KERNEL_SIZE_GAUS = Size(3.0, 3.0)
    //endregion

    //region FD
    const val STEP_SIZE = 24
    //endregion

    //region ALL
    const val TRESHOLD_RED = 100.0
    //endregion

    const val MAX_KERNEL_LENGTH = 3

    const val GRADIENT_THRESHOLD = 0.0
    const val AVG_LINE_IDX = 100
    const val AVG_COLOR_AREA = 50
}