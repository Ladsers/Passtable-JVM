package com.ladsers.passtable.jvm.processor

import com.ladsers.passtable.jvm.osMac
import com.ladsers.passtable.jvm.osWindows
import com.ladsers.passtable.jvm.printers.print
import com.ladsers.passtable.jvm.tb
import com.ladsers.passtable.lib.DataTable

object DataSetter {
    fun add(table: DataTable) {
        val note = readData("edit_note", "edit_noteOnlyLatin")
        val username = readData("edit_username", "edit_usernameOnlyLatin")
        val password = readPassword()

        tb.print("edit_tag")
        val tag = tagEncoder(readLine()!!)

        when (table.add(tag, note, username, password)) {
            0 -> table.print()
            1 -> tb.println("msg_addingDataError")
            3 -> tb.println("msg_addingInvalidChars")
        }
    }

    private fun readData(editKey: String, editOnlyLatinKey: String): String {
        var data: String
        while (true) {
            tb.print(if (!osWindows) editKey else editOnlyLatinKey)
            data = readLine()!!
            if (!DataVerifier.verifyAllowedChars(data)) continue
            return data
        }
    }

    private fun readPassword(): String {
        var password: String
        while (true) {
            tb.print(if (!osWindows && !osMac) "edit_password" else "edit_passwordOnlyLatin")
            val passRead = System.console()?.readPassword() ?: readLine()
            password = if (passRead is CharArray) String(passRead) else passRead.toString()
            if (!DataVerifier.verifyAllowedChars(password)) continue
            if (osMac && password.contains("[^ -~]".toRegex())) {
                // on the first call readPassword(), user can enter non-latin chars, on the second it is no longer possible.
                tb.println("msg_passwordProblemMac")
                continue
            }
            if (password.isEmpty() || confirmPassword(password)) break
        }
        return password
    }

    private fun confirmPassword(password: String): Boolean {
        tb.print("edit_confirm")
        val confirmRead = System.console()?.readPassword() ?: readLine()
        val confirm = if (confirmRead is CharArray) String(confirmRead) else confirmRead.toString()
        if (confirm.isNotEmpty() && password != confirm) {
            tb.println("msg_passwordsDoNotMatch")
            return false
        }
        return true
    }

    fun edit(command: List<String>, table: DataTable) {
        if (!DataVerifier.verifyCmd(command)) return
        val id = DataVerifier.verifyAndGetId(command[0]) ?: return
        val data = if (command.size > 2) command.subList(2, command.size).joinToString(" ") else ""
        if (!DataVerifier.verifyAllowedChars(data)) return
        val resCode = when (command[1]) {
            tb.key("dt_note"), tb.key("dt_note2") -> table.setNote(id, data)
            tb.key("dt_username"), tb.key("dt_username2") -> table.setUsername(id, data)
            tb.key("dt_password"), tb.key("dt_password2") -> table.setPassword(id, data)
            tb.key("dt_tag"), tb.key("dt_tag2") -> table.setTag(id, tagEncoder(data))
            else -> {
                tb.println("msg_invalidCmd")
                return
            }
        }
        when (resCode) {
            0 -> table.print()
            1 -> tb.println("msg_addingInvalidChars")
            2 -> tb.println("msg_addingDataError")
            -2 -> tb.println("msg_noEntry")
            -1 -> tb.println("msg_exception")
        }
    }

    fun delete(id: String, table: DataTable) {
        if (!DataVerifier.verifyCmd(id)) return
        val intId = DataVerifier.verifyAndGetId(id) ?: return
        when (table.delete(intId)) {
            0 -> table.print()
            -2 -> tb.println("msg_noEntry")
            -1 -> tb.println("msg_exception")
        }
    }
}