package com.ladsers.passtable.jvm.processor

import com.ladsers.passtable.jvm.tb
import com.ladsers.passtable.lib.DataTable
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection

object DataGetter {
    fun showItem(id: String, table: DataTable) {
        if (!DataVerifier.verifyCmd(id)) return
        val intId = DataVerifier.verifyAndGetId(id) ?: return

        val note = table.getNote(intId)
        val username = table.getUsername(intId)
        if (!DataVerifier.verifyGet(note, username)) return

        if (note.isNotBlank()) println("${tb.key("title_note")}:\n$note")
        if (username.isNotBlank()) println("${tb.key("title_username")}:\n$username")
        if (table.getPassword(intId).isNotEmpty()) {
            val passwordInfo = tb.key("msg_showPassword").format(id)
            println("${tb.key("title_password")}:\n$passwordInfo")
        }
    }

    fun showPassword(id: String, table: DataTable) {
        if (!DataVerifier.verifyCmd(id)) return
        val intId = DataVerifier.verifyAndGetId(id) ?: return
        when(val str = table.getPassword(intId)){
            "" -> tb.println("msg_noPassword")
            DataVerifier.strOutOfBounds -> tb.println("msg_noEntry")
            DataVerifier.strUnhandledException -> tb.println("msg_exception")
            else -> println(str)
        }
    }

    fun copy(command: List<String>, table: DataTable) {
        if (!DataVerifier.verifyCmd(command)) return
        val id = DataVerifier.verifyAndGetId(command[0]) ?: return
        val str = when (command[1]) {
            tb.key("dt_note"), tb.key("dt_note2") -> table.getNote(id)
            tb.key("dt_username"), tb.key("dt_username2") -> table.getUsername(id)
            tb.key("dt_password"), tb.key("dt_password2") -> table.getPassword(id)
            else -> {
                tb.println("msg_invalidCmd")
                return
            }
        }
        if (!DataVerifier.verifyGet(str)) return

        preloadAwtForHeadless() //makes it possible to run on JRE headless

        val selection = StringSelection(str)
        try {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
            tb.println("msg_copied")
        } catch (e: Error) { //AWTError
            tb.println("msg_copyError")
        } catch (e: Exception) { //HeadlessException
            tb.println("msg_copyError")
        }
    }

    fun logAndPass(id: String, table: DataTable) {
        if (!DataVerifier.verifyCmd(id)) return
        val intId = DataVerifier.verifyAndGetId(id) ?: return

        val username = table.getUsername(intId)
        val password = table.getPassword(intId)
        if (!DataVerifier.verifyGet(username, password)) return

        if (username.isBlank() && password.isEmpty()){
            tb.println("msg_logPassCanceled")
            return
        }

        preloadAwtForHeadless() //makes it possible to run on JRE headless

        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            runLogAndPassCopy(username, clipboard, "msg_logPassUsername", "msg_noUsername")
            runLogAndPassCopy(password, clipboard, "msg_logPassPassword", "msg_noPassword")
            val selection = StringSelection("")
            clipboard.setContents(selection, selection)
        } catch (e: Error) { //AWTError
            println(tb.key("msg_logPassUnavailable").format(tb.key("msg_copyError")))
        } catch (e: Exception) { //HeadlessException
            println(tb.key("msg_logPassUnavailable").format(tb.key("msg_copyError")))
        }
    }

    private fun preloadAwtForHeadless(){
        try {
            val emptySelection = StringSelection("")
            Toolkit.getDefaultToolkit().systemClipboard.setContents(emptySelection, emptySelection)
        } catch (e: Error) { //AWTError
        } catch (e: Exception) { //HeadlessException
        }
    }

    private fun runLogAndPassCopy(data: String, clipboard: Clipboard, infoKey: String, noDataKey: String) {
        if (data.isNotBlank()) {
            val selection = StringSelection(data)
            clipboard.setContents(selection, selection)
            tb.print(infoKey)
            readLine()
        } else tb.println(noDataKey)
    }
}