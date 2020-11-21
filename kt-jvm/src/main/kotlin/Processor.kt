import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.io.File
import java.lang.Exception

class Processor {
    companion object {
        private var table: DataTable? = null

        fun main() {
            while (true) {
                val strs = readLine()?.split(" ") ?: continue
                val send = if (strs.size > 1) strs.subList(1, strs.size)
                else listOf("/error")
                when (strs[0].decapitalize()) {
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
                    tb.key("c_password"), tb.key("c_ps") -> showPassword(send[0])
                    tb.key("c_search"), tb.key("c_s") -> search(send)
                    tb.key("c_bytag"), tb.key("c_bt") -> byTag(send[0])
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
                    table!!.print(table!!.searchByTag(tagEncoder(tag)), true)
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

            table!!.print(table!!.searchByData(trigger), true)
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
            val str = table!!.getData(intId, "p")
            if (str == "/error: outOfBounds") {
                println(tb.key("msg_noentry")); return
            }
            if (str == "/error: unhandledException") {
                println(tb.key("msg_exception")); return
            }
            println(str)
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
                tb.key("dt_note"), tb.key("dt_n") -> table!!.getData(id, "n")
                tb.key("dt_login"), tb.key("dt_l") -> table!!.getData(id, "l")
                tb.key("dt_password"), tb.key("dt_p") -> table!!.getData(id, "p")
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

            val selection = StringSelection(str)
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(selection, selection)
            println(tb.key("msg_copied"))
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
                println(tb.key("msg_invalid")); return
            }
            val data = if (command.size > 2) command.subList(2, command.size).joinToString(" ")
            else ""
            val resCode = when (command[1]) {
                tb.key("dt_note"), tb.key("dt_n") -> table!!.setData(id, "n", data)
                tb.key("dt_login"), tb.key("dt_l") -> table!!.setData(id, "l", data)
                tb.key("dt_password"), tb.key("dt_p") -> table!!.setData(id, "p", data)
                tb.key("dt_tag"), tb.key("dt_t") -> table!!.setData(id, "t", tagEncoder(data))
                else -> {
                    println(tb.key("msg_invalid")); return
                }
            }
            if (resCode == -2) {
                println(tb.key("msg_noentry")); return
            }
            if (resCode == -1) {
                println(tb.key("msg_exception")); return
            }
            table!!.print()
        }

        private fun add() {
            var note: String
            var login: String
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
                print(tb.key("edit_login"))
                login = readLine()!!
                if (login.contains('\t')) {
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
                break
            }
            print(tb.key("edit_tag"))
            tag = tagEncoder(readLine()!!)
            if (tag.contains('\t')) tag = ""

            table!!.add(tag, note, login, password)
            table!!.print()
        }

        private fun rollBack() {
            table!!.rollback()
            table!!.print()
        }

        private fun save(isSaveAs: Boolean = false): Boolean {
            val resCode = if (isSaveAs) {
                println(tb.key("msg_enternewdata"))
                table!!.save(askPathConsole(), askPasswordConsole())
            } else table!!.save()
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
                4 -> {
                    println(tb.key("msg_emptytable"))
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

        fun openProcess(filePath: String, masterPass: String? = null) {
            var path = filePath
            var master = masterPass
            if (!path.endsWith(".passtable")) path += ".passtable"
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
            if (table != null) {
                when (table!!.open()) {
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
            } else {
                println(tb.key("msg_incorrectinit"))
                quickStart()
                return
            }

            while (true) {
                table = DataTableConsole(path, master ?: askPasswordConsole(), cryptData)
                when (table!!.open()) {
                    0 -> {
                        table!!.print()
                        break
                    }
                    3 -> {
                        println(tb.key("msg_invalidpass"))
                        master = null
                        continue
                    }
                }
            }
        }

        private fun new() {
            if (!protectionUnsaved()) return
            table = DataTableConsole(path = askPathConsole())
            if (table != null) table!!.print()
            else {
                println(tb.key("msg_incorrectinit"))
                quickStart()
            }
        }

        private fun en() {
            tb.changeLocale("en")
            println(tb.key("msg_lang"))
        }

        private fun ru() {
            tb.changeLocale("ru")
            println(tb.key("msg_lang"))
        }

        private fun default() {
            println(tb.key("msg_unknown"))
        }
    }
}