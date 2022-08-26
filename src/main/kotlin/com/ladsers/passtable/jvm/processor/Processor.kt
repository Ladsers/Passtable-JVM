package com.ladsers.passtable.jvm.processor

import com.ladsers.passtable.jvm.*
import com.ladsers.passtable.jvm.printers.InfoPrinter
import com.ladsers.passtable.jvm.printers.print
import com.ladsers.passtable.lib.DataTable
import com.ladsers.passtable.lib.licenseText
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.util.*

object Processor {
    private var table: DataTable? = null

    private const val strError = "/error"
    private const val strUnhandledException = "/error: unhandledException"
    private const val strOutOfBounds = "/error: outOfBounds"

    fun main() {
        while (true) {
            print("> ")
            val strs = readLine()?.split(" ") ?: return
            val send = if (strs.size > 1) strs.subList(1, strs.size)
            else listOf(strError)
            when (strs[0].replaceFirstChar { it.lowercase(Locale.getDefault()) }) {
                tb.key("c_help"), "h", "/h", "commands" -> InfoPrinter.printHelp()
                tb.key("c_helpWithExamples") -> InfoPrinter.printHelp(true)
                tb.key("c_en") -> changeLanguage("en")
                tb.key("c_ru") -> changeLanguage("ru")
                tb.key("c_license") -> println(licenseText)
                tb.key("c_about") -> InfoPrinter.printAbout()
                tb.key("c_version"), tb.key("c_version2") -> println(version)

                tb.key("c_new") -> new()
                tb.key("c_open"), tb.key("c_open2") -> open(send)
                tb.key("c_save"), tb.key("c_save2") -> save()
                tb.key("c_saveAs") -> save(true)
                tb.key("c_rollback") -> rollBack()
                tb.key("c_clear"), tb.key("c_clear2") -> {
                    clear()
                    continue
                }

                tb.key("c_add"), tb.key("c_add2") -> add()
                tb.key("c_edit"), tb.key("c_edit2") -> edit(send)
                tb.key("c_delete"), tb.key("c_delete2") -> delete(send[0])
                tb.key("c_copy"), tb.key("c_copy2") -> copy(send)
                tb.key("c_show"), tb.key("c_show2") -> showItem(send[0])
                tb.key("c_password"), tb.key("c_password2") -> showPassword(send[0])
                tb.key("c_search"), tb.key("c_search2") -> search(send)
                tb.key("c_byTag"), tb.key("c_byTag2") -> byTag(send[0])
                tb.key("c_logPass") -> lognpass(send[0])
                tb.key("c_table"), tb.key("c_table2") -> showTable()

                tb.key("c_quit"), tb.key("c_quit2") -> if (protectionUnsaved()) return
                "" -> continue
                else -> default()
            }
            println()
        }
    }

    fun quickStart() {
        table = DataTableConsole()
        table!!.print()
    }

    private fun protectionUnsaved(): Boolean {
        if (table!!.isSaved) return true
        tb.println("msg_unsavedChangesProtection")
        print("> ")
        val com = readLine() ?: return false
        if (com == tb.key("c_yes2")){
            return if (save()) {
                println()
                true
            } else {
                tb.println("msg_canceled")
                false
            }
        }
        if (com == tb.key("c_no2")){
            println()
            return true
        }
        tb.println("msg_canceled")
        return false
    }

    private fun showTable() = table!!.print()

    private fun verifySearch(data: String): Boolean {
        return if (data == strError) {
            tb.println("msg_invalidCmd")
            false
        } else if (data.isEmpty()) {
            tb.println("msg_emptySearchQuery")
            false
        } else true
    }

    private fun checkId(integer: String): Int? {
        return try {
            integer.toInt() - 1
        } catch (e: NumberFormatException) {
            tb.println("msg_invalidCmd")
            null
        }
    }

    private fun verifyTabAndWindows(data: String): Boolean {
        return if (data.contains('\t')) {
            tb.println("msg_tabCharError")
            false
        } else if (osWindows && data.contains("[^ -~]".toRegex())) {
            tb.println("msg_nonLatinProblemWindows")
            false
        } else true
    }

    private fun verifyGet(data1: String, data2: String = ""): Boolean {
        return if (data1 == strOutOfBounds) {
            tb.println("msg_noEntry")
            false
        } else if (data1 == strUnhandledException || data2 == strUnhandledException) {
            tb.println("msg_exception")
            false
        } else true
    }

    private fun firstCheck(id: String): Boolean {
        return if (id.isEmpty() || id == strError) {
            tb.println("msg_invalidCmd")
            false
        } else true
    }

    private fun firstCheck(command: List<String>): Boolean {
        return if (command.size <= 1) {
            tb.println("msg_invalidCmd")
            false
        } else true
    }

    private fun byTag(tag: String) {
        if (!verifySearch(tag)) return
        if (tagEncoder(tag, false) != "0") {
            table!!.print(table!!.searchByTag(tagEncoder(tag)), true, searchMode = true)
        } else tb.println("msg_invalidTag")
    }

    private fun search(data: List<String>) {
        val trigger = data.joinToString(separator = " ")
        if (!verifySearch(trigger)) return
        table!!.print(table!!.searchByData(trigger), true, searchMode = true)
    }

    private fun showPassword(id: String) {
        if (!firstCheck(id)) return
        val intId = checkId(id) ?: return
        when(val str = table!!.getPassword(intId)){
            "" -> tb.println("msg_noPassword")
            strOutOfBounds -> tb.println("msg_noEntry")
            strUnhandledException -> tb.println("msg_exception")
            else -> println(str)
        }
    }

    private fun copy(command: List<String>) {
        if (!firstCheck(command)) return
        val id = checkId(command[0]) ?: return
        val str = when (command[1]) {
            tb.key("dt_note"), tb.key("dt_note2") -> table!!.getNote(id)
            tb.key("dt_username"), tb.key("dt_username2") -> table!!.getUsername(id)
            tb.key("dt_password"), tb.key("dt_password2") -> table!!.getPassword(id)
            else -> {
                tb.println("msg_invalidCmd")
                return
            }
        }
        if (!verifyGet(str)) return

        //makes it possible to run on JRE headless
        preloadAwtForHeadless()

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

    private fun delete(id: String) {
        if (!firstCheck(id)) return
        val intId = checkId(id) ?: return
        when (table!!.delete(intId)) {
            0 -> table!!.print()
            -2 -> tb.println("msg_noEntry")
            -1 -> tb.println("msg_exception")
        }
    }

    private fun edit(command: List<String>) {
        if (!firstCheck(command)) return
        val id = checkId(command[0]) ?: return
        val data = if (command.size > 2) command.subList(2, command.size).joinToString(" ") else ""
        if (!verifyTabAndWindows(data)) return
        val resCode = when (command[1]) {
            tb.key("dt_note"), tb.key("dt_note2") -> table!!.setNote(id, data)
            tb.key("dt_username"), tb.key("dt_username2") -> table!!.setUsername(id, data)
            tb.key("dt_password"), tb.key("dt_password2") -> table!!.setPassword(id, data)
            tb.key("dt_tag"), tb.key("dt_tag2") -> table!!.setTag(id, tagEncoder(data))
            else -> {
                tb.println("msg_invalidCmd")
                return
            }
        }
        when (resCode){
            0 -> table!!.print()
            1 -> tb.println("msg_addingInvalidChars")
            2 -> tb.println("msg_addingDataError")
            -2 -> tb.println("msg_noEntry")
            -1 -> tb.println("msg_exception")
        }
    }

    private fun add() {
        var note: String
        while (true) {
            tb.print(if (!osWindows) "edit_note" else "edit_noteOnlyLatin")
            note = readLine()!!
            if (!verifyTabAndWindows(note)) continue
            break
        }
        var username: String
        while (true) {
            tb.print(if (!osWindows) "edit_username" else "edit_usernameOnlyLatin")
            username = readLine()!!
            if (!verifyTabAndWindows(username)) continue
            break
        }
        var password: String
        while (true) {
            tb.print(if (!osWindows && !osMac) "edit_password" else "edit_passwordOnlyLatin")
            val passRead = System.console()?.readPassword() ?: readLine()
            password = if (passRead is CharArray) String(passRead) else passRead.toString()
            if (!verifyTabAndWindows(password)) continue
            if (osMac && password.contains("[^ -~]".toRegex())) {
                // on the first call readPassword(), user can enter non-latin chars, on the second it is no longer possible.
                tb.println("msg_passwordProblemMac")
                continue
            }
            if (password.isEmpty()) break

            tb.print("edit_confirm")
            val confirmRead = System.console()?.readPassword() ?: readLine()
            val confirm = if (confirmRead is CharArray) String(confirmRead) else confirmRead.toString()
            if (confirm.isNotEmpty() && password != confirm) {
                tb.println("msg_passwordsDoNotMatch")
                continue
            }
            break
        }
        tb.print("edit_tag")
        val tag: String = tagEncoder(readLine()!!)

        when (table!!.add(tag, note, username, password)){
            0 -> table!!.print()
            1 -> tb.println("msg_addingDataError")
            3 -> tb.println("msg_addingInvalidChars")
        }
    }

    private fun rollBack() {
        table!!.fill()
        table!!.print()
    }

    private fun save(isSaveAs: Boolean = false): Boolean {
        var resCode = if (isSaveAs) {
            tb.println("msg_enterNewPathAndPassword")
            table!!.save(askFilePath(), askPrimaryPassword(true))
        } else table!!.save()
        while (resCode == 5 || resCode == 6){
            if (resCode == 5) resCode = table!!.save(newPath = askFilePath())
            if (resCode == 6) resCode = table!!.save(newPrimaryPass = askPrimaryPassword(true))
        }
        when (resCode) {
            0 -> {
                tb.println("msg_success")
                return true
            }

            2, -2 -> print(tb.key("msg_saveError").format(tb.key("msg_encryptionError")))
            3 -> {
                tb.println("msg_savedToAppDir")
                return true
            }

            -3 -> print(tb.key("msg_saveError").format(tb.key("msg_writingToFileError")))

        }
        return false
    }

    private fun open(path: List<String>) {
        val filePath = path.joinToString(separator = " ")
        if (filePath == strError) {
            tb.println("msg_invalidCmd")
            return
        }
        if (filePath.isEmpty()) {
            tb.println("msg_nameEmpty")
            return
        }
        if (!protectionUnsaved()) return
        table = null
        openProcess(filePath)
    }

    fun openProcess(filePath: String, primaryPassword: String? = null) {
        val path = fixPath(filePath)
        var password = primaryPassword
        val cryptData: String
        try {
            cryptData = File(path).readText()
        } catch (e: Exception) {
            tb.println("msg_openFail")
            if (table == null) quickStart()
            return
        }
        /* Testing for errors in the file. */
        table = DataTableConsole(path, "/test", cryptData)

        when (table!!.fill()) {
            2 -> {
                tb.println("msg_needAppUpdate")
                quickStart()
                return
            }

            -2 -> {
                tb.println("msg_fileDamaged")
                quickStart()
                return
            }
        }

        while (true) {
            table = DataTableConsole(path, password ?: askPrimaryPassword(), cryptData)
            when (table!!.fill()) {
                0 -> {
                    table!!.print()
                    break
                }
                3 -> {
                    tb.println("msg_incorrectPassword")
                    password = null
                    continue
                }
            }
        }
    }

    private fun new() {
        if (!protectionUnsaved()) return
        table = DataTableConsole()
        table!!.print()
    }

    private fun changeLanguage(language: String) {
        if (!osWindows && (language == "en" || language == "ru")) {
            tb.changeLocale(language)
            tb.println("msg_language")
        } else tb.println("msg_unknownCmd")
    }

    private fun showItem(id: String) {
        if (!firstCheck(id)) return
        val intId = checkId(id) ?: return

        val note = table!!.getNote(intId)
        val username = table!!.getUsername(intId)
        if (!verifyGet(note, username)) return

        if (note.isNotBlank()) println("${tb.key("title_note")}:\n$note")
        if (username.isNotBlank()) println("${tb.key("title_username")}:\n$username")
        if (table!!.getPassword(intId).isNotEmpty()) {
            val passwordInfo = tb.key("msg_showPassword").format(id)
            println("${tb.key("title_password")}:\n$passwordInfo")
        }
    }

    private fun lognpass(id: String) {
        if (!firstCheck(id)) return
        val intId = checkId(id) ?: return

        val username = table!!.getUsername(intId)
        val password = table!!.getUsername(intId)
        if (!verifyGet(username, password)) return

        if (username.isBlank() && password.isEmpty()){
            tb.println("msg_logPassCanceled")
            return
        }

        preloadAwtForHeadless()

        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard

            fun runCopy(data: String, infoKey: String, infoNo: String) {
                if (data.isNotBlank()) {
                    val selection = StringSelection(data)
                    clipboard.setContents(selection, selection)
                    tb.print(infoKey)
                    readLine()
                } else tb.println(infoNo)
            }
            runCopy(username, "msg_logPassUsername", "msg_noUsername")
            runCopy(password, "msg_logPassPassword", "msg_noPassword")

            val selection = StringSelection("")
            clipboard.setContents(selection, selection)
        } catch (e: Error) { //AWTError
            println(tb.key("msg_logPassUnavailable").format(tb.key("msg_copyError")))
        } catch (e: Exception) { //HeadlessException
            println(tb.key("msg_logPassUnavailable").format(tb.key("msg_copyError")))
        }
    }

    private fun clear() {
        val processBuilder = if (!osWindows) ProcessBuilder("clear") else ProcessBuilder("cmd", "/c", "cls")
        processBuilder.inheritIO().start().waitFor()
        print("Passtable")
    }

    private fun default() = tb.println("msg_unknownCmd")

    private fun preloadAwtForHeadless(){
        try {
            val emptySelection = StringSelection("")
            Toolkit.getDefaultToolkit().systemClipboard.setContents(emptySelection, emptySelection)
        } catch (e: Error) { //AWTError
        } catch (e: Exception) { //HeadlessException
        }
    }
}