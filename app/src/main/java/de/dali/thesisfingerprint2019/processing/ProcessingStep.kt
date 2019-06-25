package de.dali.thesisfingerprint2019.processing

import org.opencv.core.Mat

abstract class ProcessingStep {

    abstract val TAG: String

    abstract fun run(originalImage: Mat): Mat

    abstract fun runReturnMultiple(originalImage: Mat): List<Mat>

    private val executionTimes: MutableList<Long> = mutableListOf()

    fun addExecutionTimes(execTime: Long) {
        executionTimes.add(execTime)
    }

    fun calcAvgExecutionTime(): Long {
        var totalExecutionTime = 0L

        executionTimes.forEach {
            totalExecutionTime += it
        }

        return totalExecutionTime / executionTimes.size
    }

    fun clearExecutionTimes() {
        executionTimes.clear()
    }
}