package de.dali.thesisfingerprint2019.processing

import org.opencv.core.CvType.CV_16S

object Config {
    //region QA
    var CENTER_SIZE_X = 50.0
    var CENTER_SIZE_Y = 50.0
    var CENTER_OFFSET_X = CENTER_SIZE_X / 2.0
    var CENTER_OFFSET_Y = CENTER_SIZE_Y / 2.0
    var GRAD_X = 0.75
    var GRAD_Y = 0.75
    var K_SIZE_GAUS = 3.0
    var K_SIZE_SOBEL = 3
    var SCALE = 1.0
    var DELTA = 3.0
    var DDEPTH = CV_16S
    var EDGE_DENS_TRESHOLD = 5.0
    //endregion

    //region ALL
    var TRESHOLD_RED = 230.0
    //endregion

    var MAX_KERNEL_LENGTH = 31
}