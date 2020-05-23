package com.cjd.dragrefresh.library

import android.util.Log
import java.util.*

/**
 * @author chenjidong
 * @email 374122600@qq.com
 * created 2020/5/7/0007
 * description 日志工具类
 */
object DragLogUtil {
    private var className: String? = null
    private var methodName: String? = null
    private var lineNumber: Int = 0

    @JvmStatic
    fun isDebuggable(): Boolean {
        return BuildConfig.DEBUG
    }

    private fun createLog(msg: String): String {
        return String.format(Locale.CHINA, "%s(%s:%d)%s", methodName, className, lineNumber, msg)
    }

    private fun getMethodName(throwable: Throwable) {
        className = throwable.stackTrace[1].fileName
        methodName = throwable.stackTrace[1].methodName
        lineNumber = throwable.stackTrace[1].lineNumber
    }

    @JvmStatic
    fun e(msg: String) {
        if (!isDebuggable())
            return
        getMethodName(Throwable())
        Log.e(className, createLog(msg))
    }

    @JvmStatic
    fun e(t: Throwable) {
        if (!isDebuggable())
            return
        getMethodName(Throwable())
        Log.e(className, createLog(Log.getStackTraceString(t)))
    }

    @JvmStatic
    fun w(msg: String) {
        if (!isDebuggable())
            return
        getMethodName(Throwable())
        Log.w(className, createLog(msg))
    }

    @JvmStatic
    fun i(msg: String) {
        if (!isDebuggable())
            return
        getMethodName(Throwable())
        Log.i(className, createLog(msg))
    }

    @JvmStatic
    fun d(msg: String) {
        if (!isDebuggable())
            return
        getMethodName(Throwable())
        Log.d(className, createLog(msg))
    }

    @JvmStatic
    fun v(msg: String) {
        if (!isDebuggable())
            return
        getMethodName(Throwable())
        Log.v(className, createLog(msg))
    }
}