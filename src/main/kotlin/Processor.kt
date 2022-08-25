import com.ladsers.passtable.lib.DataTable
import com.ladsers.passtable.lib.licenseText
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.util.*

object Processor {
    private var table: DataTable? = null

    fun main() {
        while (true) {
            print("> ")
            val strs = readLine()?.split(" ") ?: return
            val send = if (strs.size > 1) strs.subList(1, strs.size)
            else listOf("/error")
            when (strs[0].replaceFirstChar { it.lowercase(Locale.getDefault()) }) {
                tb.key("c_help"), "h", "/h", "commands" -> printCommandsList()
                tb.key("c_helpWithExamples") -> printCommandsList(true)
                tb.key("c_en") -> en()
                tb.key("c_ru") -> ru()
                tb.key("c_license") -> println(licenseText)
                tb.key("c_about") -> aboutText()
                tb.key("c_version"), tb.key("c_version2") -> println(version)

                tb.key("c_new") -> new()
                tb.key("c_open"), tb.key("c_open2") -> open(send)
                tb.key("c_save"), tb.key("c_save2") -> save()
                tb.key("c_saveAs") -> save(true)
                tb.key("c_rollback") -> rollBack()
                tb.key("c_clear"), tb.key("c_clear2") -> {
                    clear(); continue
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
        if (!table!!.isSaved) {
            while (true) {
                println(tb.key("msg_unsavedChangesProtection"))
                print("> ")
                val com = readLine() ?: continue
                when (com) {
                    tb.key("c_yes2") -> {
                        return if (save()) {
                            println()
                            true
                        } else {
                            println(tb.key("msg_canceled"))
                            false
                        }
                    }
                    tb.key("c_no2") -> {
                        println(); return true
                    }
                    tb.key("c_cancel") -> {
                        println(tb.key("msg_canceled")); return false
                    }
                    else -> println(tb.key("msg_unknownCmd"))
                }
            }
        }
        return true
    }

    private fun showTable() {
        table!!.print()
    }

    private fun byTag(tag: String) {
        if (tag == "/error") {
            println(tb.key("msg_invalidCmd")); return
        }
        if (tag.isEmpty()) {
            println(tb.key("msg_emptySearchQuery")); return
        }
        when (tag) {
            tb.key("tg_red"), tb.key("tg_red2"), tb.key("tg_green"), tb.key("tg_green2"),
            tb.key("tg_blue"), tb.key("tg_blue2"), tb.key("tg_yellow"), tb.key("tg_yellow2"),
            tb.key("tg_purple"), tb.key("tg_purple2") ->
                table!!.print(table!!.searchByTag(tagEncoder(tag)), true, searchMode = true)
            else -> println(tb.key("msg_invalidTag"))
        }
    }

    private fun search(data: List<String>) {
        val trigger = data.joinToString(separator = " ")
        if (trigger == "/error") {
            println(tb.key("msg_invalidCmd")); return
        }
        if (trigger.isEmpty()) {
            println(tb.key("msg_emptySearchQuery")); return
        }

        table!!.print(table!!.searchByData(trigger), true, searchMode = true)
    }

    private fun showPassword(id: String) {
        if (id.isEmpty() || id == "/error") {
            println(tb.key("msg_invalidCmd")); return
        }
        val intId: Int
        try {
            intId = id.toInt() - 1
        } catch (e: NumberFormatException) {
            println(tb.key("msg_invalidCmd"))
            return
        }

        when(val str = table!!.getPassword(intId)){
            "" -> println(tb.key("msg_noPassword"))
            "/error: outOfBounds" -> println(tb.key("msg_noEntry"))
            "/error: unhandledException" -> println(tb.key("msg_exception"))
            else -> println(str)
        }
    }

    private fun copy(command: List<String>) {
        if (command.isEmpty() || command[0] == "/error") {
            println(tb.key("msg_invalidCmd")); return
        }
        val id: Int
        try {
            id = command[0].toInt() - 1
        } catch (e: NumberFormatException) {
            println(tb.key("msg_invalidCmd"))
            return
        }
        if (command.size <= 1) {
            println(tb.key("msg_invalidCmd")); return
        }
        val str = when (command[1]) {
            tb.key("dt_note"), tb.key("dt_note2") -> table!!.getNote(id)
            tb.key("dt_username"), tb.key("dt_username2") -> table!!.getUsername(id)
            tb.key("dt_password"), tb.key("dt_password2") -> table!!.getPassword(id)
            else -> {
                println(tb.key("msg_invalidCmd")); return
            }
        }
        if (str == "/error: outOfBounds") {
            println(tb.key("msg_noEntry")); return
        }
        if (str == "/error: unhandledException") {
            println(tb.key("msg_exception")); return
        }

        //makes it possible to run on JRE headless
        preloadAwtForHeadless()

        val selection = StringSelection(str)
        try {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
            println(tb.key("msg_copied"))
        } catch (e: Error) { //AWTError
            println(tb.key("msg_copyError"))
        } catch (e: Exception) { //HeadlessException
            println(tb.key("msg_copyError"))
        }
    }

    private fun delete(id: String) {
        if (id.isEmpty() || id == "/error") {
            println(tb.key("msg_invalidCmd")); return
        }
        val intId: Int
        try {
            intId = id.toInt()
        } catch (e: NumberFormatException) {
            println(tb.key("msg_invalidCmd"))
            return
        }
        when (table!!.delete(intId - 1)) {
            0 -> table!!.print()
            -2 -> println(tb.key("msg_noEntry"))
            -1 -> println(tb.key("msg_exception"))
        }
    }

    private fun edit(command: List<String>) {
        if (command.isEmpty() || command[0] == "/error") {
            println(tb.key("msg_invalidCmd")); return
        }
        val id: Int
        try {
            id = command[0].toInt() - 1
        } catch (e: NumberFormatException) {
            println(tb.key("msg_invalidCmd"))
            return
        }
        if (command.size <= 1) {
            println(tb.key("msg_invalidCmd"))
            return
        }
        val data = if (command.size > 2) command.subList(2, command.size).joinToString(" ") else ""
        if (osWindows && data.contains("[^ -~]".toRegex())) {
            println(tb.key("msg_nonLatinProblemWindows"))
            return
        }
        val resCode = when (command[1]) {
            tb.key("dt_note"), tb.key("dt_note2") -> table!!.setNote(id, data)
            tb.key("dt_username"), tb.key("dt_username2") -> table!!.setUsername(id, data)
            tb.key("dt_password"), tb.key("dt_password2") -> table!!.setPassword(id, data)
            tb.key("dt_tag"), tb.key("dt_tag2") -> table!!.setTag(id, tagEncoder(data))
            else -> {
                println(tb.key("msg_invalidCmd"))
                return
            }
        }
        when (resCode){
            0 -> table!!.print()
            2 -> println(tb.key("msg_addingError"))
            -2 -> println(tb.key("msg_noEntry"))
            -1 -> println(tb.key("msg_exception"))
        }
    }

    private fun add() {
        var note: String
        var username: String
        var password: String
        var tag: String

        while (true) {
            print(tb.key(if (!osWindows) "edit_note" else "edit_noteOnlyLatin"))
            note = readLine()!!
            if (osWindows && note.contains("[^ -~]".toRegex())) {
                println(tb.key("msg_nonLatinProblemWindows")); continue
            }
            if (note.contains('\t')) {
                println(tb.key("msg_tabCharError")); continue
            }
            break
        }
        while (true) {
            print(tb.key(if (!osWindows) "edit_username" else "edit_usernameOnlyLatin"))
            username = readLine()!!
            if (osWindows && username.contains("[^ -~]".toRegex())) {
                println(tb.key("msg_nonLatinProblemWindows")); continue
            }
            if (username.contains('\t')) {
                println(tb.key("msg_tabCharError")); continue
            }
            break
        }
        while (true) {
            print(tb.key(if (!osWindows && !osMac) "edit_password" else "edit_passwordOnlyLatin"))
            val passRead = System.console()?.readPassword() ?: readLine()
            password = if (passRead is CharArray) String(passRead) else passRead.toString()
            if (password.contains("[^ -~]".toRegex())){
                if (osWindows) {
                    println(tb.key("msg_nonLatinProblemWindows"))
                    continue
                }
                if (osMac) { // on the first call readPassword(), user can enter non-latin chars, on the second it is no longer possible.
                    println(tb.key("msg_passwordProblemMac"))
                    continue
                }
            }
            if (password.contains('\t')) {
                println(tb.key("msg_tabCharError")); continue
            }
            if (password.isEmpty()) break

            print(tb.key("edit_confirm"))
            val confirmRead = System.console()?.readPassword() ?: readLine()
            val confirm = if (confirmRead is CharArray) String(confirmRead) else confirmRead.toString()
            if (confirm.isNotEmpty() && password != confirm) {
                println(tb.key("msg_passwordsDoNotMatch")); continue
            }
            break
        }
        print(tb.key("edit_tag"))
        tag = tagEncoder(readLine()!!)
        if (tag.contains('\t')) tag = ""

        when (table!!.add(tag, note, username, password)){
            0 -> table!!.print()
            1 -> println(tb.key("msg_addingError"))
        }
    }

    private fun rollBack() {
        table!!.fill()
        table!!.print()
    }

    private fun save(isSaveAs: Boolean = false): Boolean {
        var resCode = if (isSaveAs) {
            println(tb.key("msg_enterNewPathAndPassword"))
            table!!.save(askFilePath(), askPrimaryPassword(true))
        } else table!!.save()
        while (resCode == 5 || resCode == 6){
            if (resCode == 5) resCode = table!!.save(newPath = askFilePath())
            if (resCode == 6) resCode = table!!.save(newPrimaryPass = askPrimaryPassword(true))
        }
        when (resCode) {
            0 -> {
                println(tb.key("msg_success"))
                return true
            }

            2, -2 -> print(tb.key("msg_saveError").format(tb.key("msg_encryptionError")))
            3 -> {
                println(tb.key("msg_savedToAppDir"))
                return true
            }

            -3 -> print(tb.key("msg_saveError").format(tb.key("msg_writingToFileError")))

        }
        return false
    }

    private fun open(path: List<String>) {
        val filePath = path.joinToString(separator = " ")
        if (filePath == "/error") {
            println(tb.key("msg_invalidCmd")); return
        }
        if (filePath.isEmpty()) {
            println(tb.key("msg_nameEmpty")); return
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
            println(tb.key("msg_openFail"))
            if (table == null) quickStart()
            return
        }
        /* Testing for errors in the file. */
        table = DataTableConsole(path, "/test", cryptData)

        when (table!!.fill()) {
            2 -> {
                println(tb.key("msg_needAppUpdate"))
                quickStart()
                return
            }

            -2 -> {
                println(tb.key("msg_fileDamaged"))
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
                    println(tb.key("msg_incorrectPassword"))
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

    private fun en() {
        if (!osWindows) {
            tb.changeLocale("en")
            println(tb.key("msg_language"))
        } else println(tb.key("msg_unknownCmd"))
    }

    private fun ru() {
        if (!osWindows) {
            tb.changeLocale("ru")
            println(tb.key("msg_language"))
        } else println(tb.key("msg_unknownCmd"))
    }

    private fun showItem(id: String) {
        if (id.isEmpty() || id == "/error") {
            println(tb.key("msg_invalidCmd")); return
        }
        val intId: Int
        try {
            intId = id.toInt() - 1
        } catch (e: NumberFormatException) {
            println(tb.key("msg_invalidCmd"))
            return
        }

        val note = table!!.getNote(intId)

        if (note == "/error: outOfBounds") {
            println(tb.key("msg_noEntry")); return
        }

        val username = table!!.getUsername(intId)

        if (note == "/error: unhandledException" || username == "/error: unhandledException") {
            println(tb.key("msg_exception")); return
        }

        if (note.isNotBlank()) println("${tb.key("title_note")}:\n$note")
        if (username.isNotBlank()) println("${tb.key("title_username")}:\n$username")
        val passwordInfo = tb.key("msg_showPassword").format(id)
        if (table!!.getPassword(intId).isNotEmpty()) println("${tb.key("title_password")}:\n$passwordInfo")
    }

    private fun lognpass(id: String) {
        if (id.isEmpty() || id == "/error") {
            println(tb.key("msg_invalidCmd")); return
        }
        val intId: Int
        try {
            intId = id.toInt() - 1
        } catch (e: NumberFormatException) {
            println(tb.key("msg_invalidCmd"))
            return
        }

        val username = table!!.getUsername(intId)

        if (username == "/error: outOfBounds") {
            println(tb.key("msg_noEntry")); return
        }

        val password = table!!.getPassword(intId)

        if (username == "/error: unhandledException" || password == "/error: unhandledException") {
            println(tb.key("msg_exception")); return
        }

        if (username.isBlank() && password.isEmpty()){
            println(tb.key("msg_logPassCanceled")); return
        }

        preloadAwtForHeadless()

        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard

            if (username.isNotBlank()) {
                val selection = StringSelection(username)
                clipboard.setContents(selection, selection)
                print(tb.key("msg_logPassUsername"))
                readLine()
            } else println(tb.key("msg_noUsername"))

            if (password.isNotEmpty()) {
                val selection = StringSelection(password)
                clipboard.setContents(selection, selection)
                print(tb.key("msg_logPassPassword"))
                readLine()
            } else println(tb.key("msg_noPassword"))

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

    private fun default() {
        println(tb.key("msg_unknownCmd"))
    }

    private fun preloadAwtForHeadless(){
        try {
            val emptySelection = StringSelection("")
            Toolkit.getDefaultToolkit().systemClipboard.setContents(emptySelection, emptySelection)
        } catch (e: Error) { //AWTError
        } catch (e: Exception) { //HeadlessException
        }
    }
}