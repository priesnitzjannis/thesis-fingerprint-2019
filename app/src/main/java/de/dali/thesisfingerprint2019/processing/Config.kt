package de.dali.thesisfingerprint2019.processing

object Config {
    //region MultiFingerDetection
    const val KERNEL_SIZE_FILTER = 15.0

    const val H_LOWER = 0.0
    const val S_LOWER = 10.0
    const val V_LOWER = 60.0

    const val H_UPPER = 20.0
    const val S_UPPER = 150.0
    const val V_UPPER = 255.0

    const val Y_LOWER = 0.0
    const val CR_LOWER = 133.0
    const val CB_LOWER = 77.0

    const val Y_UPPER = 255.0
    const val CR_UPPER = 173.0
    const val CB_UPPER = 127.0

    const val KERNEL_SIZE_FAND = 28.0

    const val MIN_AREA_SIZE = 60000
    //endregion

    //region FingerRotationImprecise
    const val POINT_PAIR_DST = 100
    //endregion

    //region FingerBorderDetection
    const val KERNEL_SIZE_BLUR = 7.0

    const val THRESHOLD_MAX = 120.0
    const val BLOCKSIZE = 31

    const val DILATE_KERNEL_SIZE = 33.0
    const val DILATE_ITERATIONS = 2

    const val ERODE_KERNEL_SIZE = 17.0
    const val ERODE_ITERATIONS = 2

    const val PIXEL_TO_CROP = 100

    //endregion

    //region FindFingerTip
    const val ROW_TO_COL_RATIO = 1.5
    //endregion

    //region MultiQA
    const val CENTER_SIZE_X = 50.0
    const val CENTER_SIZE_Y = 50.0
    const val CENTER_OFFSET_X = CENTER_SIZE_X / 2.0
    const val CENTER_OFFSET_Y = CENTER_SIZE_Y / 2.0

    const val KERNEL_SIZE_GAUS = 3.0

    const val K_SIZE_SOBEL = 3
    const val SCALE = 1.0
    const val DELTA = 3.0

    const val GRAD_X = 0.75
    const val GRAD_Y = 0.75
    //endregion

    //region Enhancement
    const val CLIP_LIMIT = 2.0
    const val CLAHE_ITERATIONS = 6

    const val GAUSSIAN_KERNEL_SIZE_LOW = 3.0
    const val GAUSSIAN_KERNEL_SIZE_HIGH = 7.0

    //endregion

    //region DEPRECATED
    const val EDGE_DENS_TRESHOLD = 10.0
    //region FD
    const val STEP_SIZE = 24
    //endregion

    //region ALL
    const val TRESHOLD_RED = 100.0
    //endregion
    //endregion
}