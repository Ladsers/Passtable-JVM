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
            val strs = readLine()?.split(" ") ?: continue
            val send = if (strs.size > 1) strs.subList(1, strs.size)
            else listOf("/error")
            when (strs[0].replaceFirstChar { it.lowercase(Locale.getDefault()) }) {
                tb.key("c_help"), "h", "/h", "commands" -> printCommandsList()
                tb.key("c_heg") -> printCommandsList(true)
                tb.key("c_en") -> en()
                tb.key("c_ru") -> ru()
                tb.key("c_license") -> println(licenseText)

                tb.key("c_new") -> new()
                tb.key("c_open"), tb.key("c_op") -> open(send)
                tb.key("c_save"), tb.key("c_sv") -> save()
                tb.key("c_saveas") -> save(true)
                tb.key("c_rollback") -> rollBack()

                tb.key("c_add"), tb.key("c_add2") -> add()
                tb.key("c_edit"), tb.key("c_ed") -> edit(send)
                tb.key("c_delete"), tb.key("c_del") -> delete(send[0])
                tb.key("c_copy"), tb.key("c_cp") -> copy(send)
                tb.key("c_show"), tb.key("c_sh") -> showItem(send[0])
                tb.key("c_password"), tb.key("c_ps") -> showPassword(send[0])
                tb.key("c_search"), tb.key("c_s") -> search(send)
                tb.key("c_bytag"), tb.key("c_bt") -> byTag(send[0])
                tb.key("c_lnp") -> lognpass(send[0])
                tb.key("c_table"), tb.key("c_t") -> showTable()

                tb.key("c_quit"), tb.key("c_q") -> if (protectionUnsaved()) return
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
                println(tb.key("msg_saveornot"))
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
                    else -> println(tb.key("msg_unknown"))
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
            println(tb.key("msg_invalid")); return
        }
        if (tag.isEmpty()) {
            println(tb.key("msg_emptysearch")); return
        }
        when (tag) {
            tb.key("tg_red"), tb.key("tg_r"), tb.key("tg_green"), tb.key("tg_g"),
            tb.key("tg_blue"), tb.key("tg_b"), tb.key("tg_yellow"), tb.key("tg_y"),
            tb.key("tg_purple"), tb.key("tg_p") ->
                table!!.print(table!!.searchByTag(tagEncoder(tag)), true, searchMode = true)
            else -> println(tb.key("msg_unknowntag"))
        }
    }

    private fun search(data: List<String>) {
        val trigger = data.joinToString(separator = " ")
        if (trigger == "/error") {
            println(tb.key("msg_invalid")); return
        }
        if (trigger.isEmpty()) {
            println(tb.key("msg_emptysearch")); return
        }

        table!!.print(table!!.searchByData(trigger), true, searchMode = true)
    }

    private fun showPassword(id: String) {
        if (id.isEmpty() || id == "/error") {
            println(tb.key("msg_invalid")); return
        }
        val intId: Int
        try {
            intId = id.toInt() - 1
        } catch (e: NumberFormatException) {
            println(tb.key("msg_invalid"))
            return
        }

        when(val str = table!!.getPassword(intId)){
            "" -> println(tb.key("msg_nopass"))
            "/error: outOfBounds" -> println(tb.key("msg_noentry"))
            "/error: unhandledException" -> println(tb.key("msg_exception"))
            else -> println(str)
        }
    }

    private fun copy(command: List<String>) {
        if (command.isEmpty() || command[0] == "/error") {
            println(tb.key("msg_invalid")); return
        }
        val id: Int
        try {
            id = command[0].toInt() - 1
        } catch (e: NumberFormatException) {
            println(tb.key("msg_invalid"))
            return
        }
        if (command.size <= 1) {
            println(tb.key("msg_invalid")); return
        }
        val str = when (command[1]) {
            tb.key("dt_note"), tb.key("dt_n") -> table!!.getNote(id)
            tb.key("dt_username"), tb.key("dt_u") -> table!!.getUsername(id)
            tb.key("dt_password"), tb.key("dt_p") -> table!!.getPassword(id)
            else -> {
                println(tb.key("msg_invalid")); return
            }
        }
        if (str == "/error: outOfBounds") {
            println(tb.key("msg_noentry")); return
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
            println(tb.key("msg_errcopy"))
        } catch (e: Exception) { //HeadlessException
            println(tb.key("msg_errcopy"))
        }
    }

    private fun delete(id: String) {
        if (id.isEmpty() || id == "/error") {
            println(tb.key("msg_invalid")); return
        }
        val intId: Int
        try {
            intId = id.toInt()
        } catch (e: NumberFormatException) {
            println(tb.key("msg_invalid"))
            return
        }
        when (table!!.delete(intId - 1)) {
            0 -> table!!.print()
            -2 -> println(tb.key("msg_noentry"))
            -1 -> println(tb.key("msg_exception"))
        }
    }

    private fun edit(command: List<String>) {
        if (command.isEmpty() || command[0] == "/error") {
            println(tb.key("msg_invalid")); return
        }
        val id: Int
        try {
            id = command[0].toInt() - 1
        } catch (e: NumberFormatException) {
            println(tb.key("msg_invalid"))
            return
        }
        if (command.size <= 1) {
            println(tb.key("msg_invalid"))
            return
        }
        val data = if (command.size > 2) command.subList(2, command.size).joinToString(" ") else ""
        val resCode = when (command[1]) {
            tb.key("dt_note"), tb.key("dt_n") -> table!!.setNote(id, data)
            tb.key("dt_username"), tb.key("dt_u") -> table!!.setUsername(id, data)
            tb.key("dt_password"), tb.key("dt_p") -> table!!.setPassword(id, data)
            tb.key("dt_tag"), tb.key("dt_t") -> table!!.setTag(id, tagEncoder(data))
            else -> {
                println(tb.key("msg_invalid"))
                return
            }
        }
        when (resCode){
            0 -> table!!.print()
            2 -> println(tb.key("msg_erradd"))
            -2 -> println(tb.key("msg_noentry"))
            -1 -> println(tb.key("msg_exception"))
        }
    }

    private fun add() {
        var note: String
        var username: String
        var password: String
        var tag: String

        while (true) {
            print(tb.key("edit_note"))
            note = readLine()!!
            if (note.contains('\t')) {
                println(tb.key("msg_tabchar")); continue
            }
            break
        }
        while (true) {
            print(tb.key("edit_username"))
            username = readLine()!!
            if (username.contains('\t')) {
                println(tb.key("msg_tabchar")); continue
            }
            break
        }
        while (true) {
            print(tb.key("edit_password"))
            val passRead = System.console()?.readPassword() ?: readLine()
            password = if (passRead is CharArray) String(passRead) else passRead.toString()
            if (password.contains('\t')) {
                println(tb.key("msg_tabchar")); continue
            }
            if (password.isEmpty()) break

            print(tb.key("edit_confirm"))
            val confirmRead = System.console()?.readPassword() ?: readLine()
            val confirm = if (confirmRead is CharArray) String(confirmRead) else confirmRead.toString()
            if (confirm.isNotEmpty() && password != confirm) {
                println(tb.key("msg_dontmatch")); continue
            }
            break
        }
        print(tb.key("edit_tag"))
        tag = tagEncoder(readLine()!!)
        if (tag.contains('\t')) tag = ""

        when (table!!.add(tag, note, username, password)){
            0 -> table!!.print()
            1 -> println(tb.key("msg_erradd"))
        }
    }

    private fun rollBack() {
        table!!.fill()
        table!!.print()
    }

    private fun save(isSaveAs: Boolean = false): Boolean {
        var resCode = if (isSaveAs) {
            println(tb.key("msg_enternewdata"))
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
            2 -> {
                print(tb.key("msg_errsave"))
                println(tb.key("msg_notsame"))
            }
            3 -> {
                println(tb.key("msg_errdirectory"))
                return true
            }
            -2 -> {
                print(tb.key("msg_errsave"))
                println(tb.key("msg_errencrypt"))
            }
            -3 -> {
                print(tb.key("msg_errsave"))
                println(tb.key("msg_errwrite"))
            }
        }
        return false
    }

    private fun open(path: List<String>) {
        val filePath = path.joinToString(separator = " ")
        if (filePath == "/error") {
            println(tb.key("msg_invalid")); return
        }
        if (filePath.isEmpty()) {
            println(tb.key("msg_emptynamefile")); return
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
            println(tb.key("msg_openfail"))
            if (table == null) quickStart()
            return
        }
        /* Testing for errors in the file. */
        table = DataTableConsole(path, "/test", cryptData)

        when (table!!.fill()) {
            2 -> {
                println(tb.key("msg_verfail"))
                quickStart()
                return
            }

            -2 -> {
                println(tb.key("msg_filecorrupted"))
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
                    println(tb.key("msg_invalidpass"))
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
            println(tb.key("msg_lang"))
        } else println(tb.key("msg_unknown"))
    }

    private fun ru() {
        if (!osWindows) {
            tb.changeLocale("ru")
            println(tb.key("msg_lang"))
        } else println(tb.key("msg_unknown"))
    }

    private fun showItem(id: String) {
        if (id.isEmpty() || id == "/error") {
            println(tb.key("msg_invalid")); return
        }
        val intId: Int
        try {
            intId = id.toInt() - 1
        } catch (e: NumberFormatException) {
            println(tb.key("msg_invalid"))
            return
        }

        val note = table!!.getNote(intId)

        if (note == "/error: outOfBounds") {
            println(tb.key("msg_noentry")); return
        }

        val username = table!!.getUsername(intId)

        if (note == "/error: unhandledException" || username == "/error: unhandledException") {
            println(tb.key("msg_exception")); return
        }

        if (note.isNotBlank()) println("${tb.key("title_note")}:\n$note")
        if (username.isNotBlank()) println("${tb.key("title_username")}:\n$username")
        val passwordInfo = "${tb.key("msg_showpassword1")} $id${tb.key("msg_showpassword2")}"
        if (table!!.getPassword(intId).isNotEmpty()) println("${tb.key("title_password")}:\n$passwordInfo")
    }

    private fun lognpass(id: String) {
        if (id.isEmpty() || id == "/error") {
            println(tb.key("msg_invalid")); return
        }
        val intId: Int
        try {
            intId = id.toInt() - 1
        } catch (e: NumberFormatException) {
            println(tb.key("msg_invalid"))
            return
        }

        val username = table!!.getUsername(intId)

        if (username == "/error: outOfBounds") {
            println(tb.key("msg_noentry")); return
        }

        val password = table!!.getPassword(intId)

        if (username == "/error: unhandledException" || password == "/error: unhandledException") {
            println(tb.key("msg_exception")); return
        }

        if (username.isBlank() && password.isEmpty()){
            println(tb.key("msg_lnpcanceled")); return
        }

        preloadAwtForHeadless()

        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard

            if (username.isNotBlank()) {
                val selection = StringSelection(username)
                clipboard.setContents(selection, selection)
                print(tb.key("msg_lnpusername"))
                readLine()
            } else println(tb.key("msg_nousername"))

            if (password.isNotEmpty()) {
                val selection = StringSelection(password)
                clipboard.setContents(selection, selection)
                print(tb.key("msg_lnppassword"))
                readLine()
            } else println(tb.key("msg_nopass"))

            val selection = StringSelection("")
            clipboard.setContents(selection, selection)
        } catch (e: Error) { //AWTError
            println(tb.key("msg_errlnp"))
            println(tb.key("msg_errcopy"))
        } catch (e: Exception) { //HeadlessException
            println(tb.key("msg_errlnp"))
            println(tb.key("msg_errcopy"))
        }
    }

    private fun default() {
        println(tb.key("msg_unknown"))
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