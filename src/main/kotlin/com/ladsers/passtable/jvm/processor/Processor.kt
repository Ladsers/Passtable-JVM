package com.ladsers.passtable.jvm.processor

import com.ladsers.passtable.jvm.DataTableJvm
import com.ladsers.passtable.jvm.osWindows
import com.ladsers.passtable.jvm.printers.InfoPrinter
import com.ladsers.passtable.jvm.printers.print
import com.ladsers.passtable.jvm.processor.DataVerifier.strError
import com.ladsers.passtable.jvm.tb
import com.ladsers.passtable.jvm.version
import com.ladsers.passtable.lib.DataTable
import com.ladsers.passtable.lib.licenseText
import java.io.File
import java.util.*

object Processor {
    private var table: DataTable? = null

    fun main() {
        while (true) {
            print("> ")
            val cmd = readLine()?.split(" ") ?: return
            val send = if (cmd.size > 1) cmd.subList(1, cmd.size)
            else listOf(strError)
            when (cmd[0].replaceFirstChar { it.lowercase(Locale.getDefault()) }) {
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
                tb.key("c_table"), tb.key("c_table2") -> showTable()
                tb.key("c_clear"), tb.key("c_clear2") -> {
                    clear()
                    continue
                }

                tb.key("c_add"), tb.key("c_add2") -> DataSetter.add(table!!)
                tb.key("c_edit"), tb.key("c_edit2") -> DataSetter.edit(send, table!!)
                tb.key("c_delete"), tb.key("c_delete2") -> DataSetter.delete(send[0], table!!)
                tb.key("c_copy"), tb.key("c_copy2") -> DataGetter.copy(send, table!!)
                tb.key("c_show"), tb.key("c_show2") -> DataGetter.showItem(send[0], table!!)
                tb.key("c_password"), tb.key("c_password2") -> DataGetter.showPassword(send[0], table!!)
                tb.key("c_logPass") -> DataGetter.logAndPass(send[0], table!!)
                tb.key("c_search"), tb.key("c_search2") -> DataSearcher.search(send, table!!)
                tb.key("c_byTag"), tb.key("c_byTag2") -> DataSearcher.byTag(send[0], table!!)

                tb.key("c_quit"), tb.key("c_quit2") -> if (protectionUnsaved()) return
                "" -> continue
                else -> default()
            }
            println()
        }
    }

    fun quickStart() {
        table = DataTableJvm()
        table!!.print()
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
        table = DataTableJvm(path, "/test", cryptData)

        try {
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
        } catch (e: Exception) {
            tb.println("msg_fileDamaged")
            quickStart()
            return
        }

        while (true) {
            table = DataTableJvm(path, password ?: askPrimaryPassword(), cryptData)
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

    private fun new() {
        if (!protectionUnsaved()) return
        table = DataTableJvm()
        table!!.print()
    }

    private fun save(isSaveAs: Boolean = false): Boolean {
        var resCode = if (isSaveAs) {
            tb.println("msg_enterNewPathAndPassword")
            table!!.save(askFilePath(), askPrimaryPassword(true))
        } else table!!.save()
        while (resCode == 5 || resCode == 6) {
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

    private fun rollBack() {
        table!!.fill()
        table!!.print()
    }

    private fun showTable() = table!!.print()

    private fun changeLanguage(language: String) {
        if (!osWindows && (language == "en" || language == "ru")) {
            tb.changeLocale(language)
            tb.println("msg_language")
        } else tb.println("msg_unknownCmd")
    }

    private fun clear() {
        val processBuilder = if (!osWindows) ProcessBuilder("clear") else ProcessBuilder("cmd", "/c", "cls")
        processBuilder.inheritIO().start().waitFor()
        print("Passtable")
    }

    private fun default() = tb.println("msg_unknownCmd")

    private fun protectionUnsaved(): Boolean {
        if (table!!.isSaved) return true
        tb.println("msg_unsavedChangesProtection")
        print("> ")
        val com = readLine() ?: return false
        if (com == tb.key("c_yes2")) {
            return if (save()) {
                println()
                true
            } else {
                tb.println("msg_canceled")
                false
            }
        }
        if (com == tb.key("c_no2")) {
            println()
            return true
        }
        tb.println("msg_canceled")
        return false
    }
}