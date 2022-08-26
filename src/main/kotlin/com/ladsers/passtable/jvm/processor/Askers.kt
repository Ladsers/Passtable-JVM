package com.ladsers.passtable.jvm.processor

import com.ladsers.passtable.jvm.osWindows
import com.ladsers.passtable.jvm.tb
import com.ladsers.passtable.lib.Verifier

fun askPrimaryPassword(forSaving: Boolean = false): String {
    while (true) {
        print(tb.key(if (!forSaving) "edit_primaryPassword" else "edit_primaryPasswordNew"))
        val passwordRead = System.console()?.readPassword() ?: readLine()
        val password = if (passwordRead is CharArray) String(passwordRead) else passwordRead.toString()
        when (Verifier.verifyPrimary(password)) {
            0 -> if (!forSaving) return password
            1 -> {
                println(tb.key("mag_primaryEmpty"))
                continue
            }

            2 -> {
                println(tb.key("msg_primaryInvalidChars").format(Verifier.getPrimaryAllowedChars(tb.key("key_space"))))
                continue
            }

            3 -> {
                println(tb.key("msg_primaryForwardSlash"))
                continue
            }

            4 -> {
                println(tb.key("msg_primaryLong"))
                continue
            }
        }

        print(tb.key("edit_primaryPasswordConfirm"))
        val confirmRead = System.console()?.readPassword() ?: readLine()
        val confirm = if (confirmRead is CharArray) String(confirmRead) else confirmRead.toString()
        if (confirm.isNotEmpty() && password != confirm) {
            println(tb.key("msg_passwordsDoNotMatch"))
            continue
        }
        return password
    }
}

fun askFilePath(): String {
    while (true){
        print(tb.key("edit_fileName"))
        val path = fixPath(readLine()!!)
        val delimiter = if (osWindows) "\\" else "/"
        val nameOfFile = path.substringAfterLast(delimiter).substringBeforeLast(".")
        when (Verifier.verifyFileName(nameOfFile)) {
            0 -> return path
            1 -> println(tb.key("msg_nameEmpty"))
            2 -> println(tb.key("msg_nameInvalidChars").format(Verifier.fileNameInvalidChars))
            3 -> println(tb.key("msg_nameSpaceChar"))
            4 -> println(tb.key("msg_nameInvalidWord").format(Verifier.fileNameInvalidWinWords))
            5 -> println(tb.key("msg_nameLong"))
        }
    }
}