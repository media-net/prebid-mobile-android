package com.app.logger

import android.util.Log

object CustomLogger {
    private val ENABLE_LOG = BuildConfig.DEBUG
    private const val default_message = ""

    private fun printLog(logLevels: LogLevels, tag: String, message: String?, throwable: Throwable? = null) {
        if (!ENABLE_LOG) return
        val messageLog: String = message ?: default_message
        when (logLevels) {
            LogLevels.ERROR -> {
                if (throwable == null) {
                    Log.e(tag, messageLog)
                } else {
                    Log.e(tag, messageLog, throwable)
                }
            }
            LogLevels.DEBUG -> {
                Log.d(tag, messageLog)
            }
            LogLevels.INFO -> {
                Log.i(tag, messageLog)
            }
            LogLevels.WARNING -> {
                Log.w(tag, messageLog)
            }
            LogLevels.VERBOSE -> {
                Log.v(tag, messageLog)
            }
            LogLevels.ASSERT -> {
                Log.println(Log.ASSERT, tag, messageLog)
            }
        }
    }

    @JvmStatic
    fun info(tag: String, message: String?) {
        printLog(LogLevels.INFO, tag, message)
    }

    @JvmStatic
    fun debug(tag: String, message: String?) {
        printLog(LogLevels.DEBUG, tag, message)
    }

    @JvmStatic
    fun error(tag: String, message: String?) {
        error(tag, message, null)
    }

    @JvmStatic
    fun verbose(tag: String, message: String?) {
        printLog(LogLevels.VERBOSE, tag, message)
    }

    @JvmStatic
    fun warning(tag: String, message: String?) {
        printLog(LogLevels.WARNING, tag, message)
    }

    @JvmStatic
    fun assertLog(tag: String, message: String?) {
        printLog(LogLevels.ASSERT, tag, message)
    }

    @JvmStatic
    fun error(tag: String, message: String?, throwable: Throwable?) {
        printLog(LogLevels.ERROR, tag, message, throwable)
    }

    /**
     * call this method for tracking the crash exception and log
     */
    @JvmStatic
    fun track(tag: String, message: String, throwable: Throwable? = null) {
        try {
            CrashlyticsHelper.log("$tag:$message")
            if (ENABLE_LOG) debug(tag, message)
            throwable?.let { CrashlyticsHelper.logError(it) }
        } catch (e: Exception) {
            if (ENABLE_LOG) {
                e.printStackTrace()
            }
        }
    }

    /**
     * call this method for tracking the crash logs
     */
    @JvmStatic
    fun track(message: String) {
        try {
            CrashlyticsHelper.log(message)
        } catch (e: Exception) {
            if (ENABLE_LOG) {
                e.printStackTrace()
            }
        }
    }
}
