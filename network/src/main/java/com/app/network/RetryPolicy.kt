package com.app.network

import kotlin.math.pow

class RetryPolicy(
    private val maxTries: Int = TOTAL_RETRY,
    private var remainingTries: Int = TOTAL_RETRY,
    private val multiplier: RetryMultiplier = RetryMultiplier.LINEAR,
    private val delayInMillis: Long = 500
) {

    companion object {
        const val TOTAL_RETRY = 3
    }

    fun reduceRetries(count: Int = 1) {
        remainingTries -= count
    }

    fun shouldTry() = remainingTries > 0

    fun getDelay():Long {
        return when (multiplier) {
            RetryMultiplier.CONSTANT -> delayInMillis
            RetryMultiplier.LINEAR -> delayInMillis * (maxTries - remainingTries)
            RetryMultiplier.EXPONENTIAL -> (delayInMillis.toDouble().pow(maxTries - remainingTries)).toLong()
        }
    }
}

enum class RetryMultiplier {
    CONSTANT, LINEAR, EXPONENTIAL
}