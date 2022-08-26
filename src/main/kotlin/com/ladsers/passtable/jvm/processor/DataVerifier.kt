package com.ladsers.passtable.jvm.processor

import com.ladsers.passtable.jvm.osWindows
import com.ladsers.passtable.jvm.tb

object DataVerifier {
    const val strError = "/error"
    const val strUnhandledException = "/error: unhandledException"
    const val strOutOfBounds = "/error: outOfBounds"

    fun verifyCmd(id: String): Boolean {
        return if (id.isEmpty() || id == strError) {
            tb.println("msg_invalidCmd")
            false
        } else true
    }

    fun verifyCmd(command: List<String>): Boolean {
        return if (command.size <= 1) {
            tb.println("msg_invalidCmd")
            false
        } else true
    }

    fun verifyAndGetId(id: String): Int? {
        return try {
            id.toInt() - 1
        } catch (e: NumberFormatException) {
            tb.println("msg_invalidCmd")
            null
        }
    }

    fun verifyAllowedChars(data: String): Boolean {
        return if (data.contains('\t')) {
            tb.println("msg_tabCharError")
            false
        } else if (osWindows && data.contains("[^ -~]".toRegex())) {
            tb.println("msg_nonLatinProblemWindows")
            false
        } else true
    }

    fun verifyGet(data1: String, data2: String = ""): Boolean {
        return if (data1 == strOutOfBounds) {
            tb.println("msg_noEntry")
            false
        } else if (data1 == strUnhandledException || data2 == strUnhandledException) {
            tb.println("msg_exception")
            false
        } else true
    }

    fun verifySearchQuery(query: String): Boolean {
        return if (query == strError) {
            tb.println("msg_invalidCmd")
            false
        } else if (query.isEmpty()) {
            tb.println("msg_emptySearchQuery")
            false
        } else true
    }
}