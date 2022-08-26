package com.ladsers.passtable.jvm.printers

import com.ladsers.passtable.jvm.osWindows
import com.ladsers.passtable.jvm.tb
import com.ladsers.passtable.jvm.version

object InfoPrinter {
    private const val webRepo = """https://github.com/Ladsers/Passtable-JVM"""
    private const val webPage = """https://www.ladsers.com/Passtable"""

    fun printHeader() {
        println(
            """
  ___  _   ___ ___ _____ _   ___ _    ___ 
 | _ \/_\ / __/ __|_   _/_\ | _ ) |  | __|
 |  _/ _ \\__ \__ \ | |/ _ \| _ \ |__| _| 
 |_|/_/ \_\___/___/ |_/_/ \_\___/____|___|
    """.trimIndent()
        )
        println()
        println("Passtable\t${tb.key("header_version").format(version)}")
        println(tb.key("header_projectPage").format(webPage))
        println(tb.key("header_projectRepo").format(webRepo))
        println()
        tb.println("header_h")
        if (!osWindows) tb.println("header_ru")
        tb.println("header_license")
    }

    fun printAbout() {
        println("Passtable (%s)".format(tb.key("about_jvmApp")))
        println(tb.key("about_version").format(version))
        tb.println("about_createdBy")
        println()

        tb.println("about_webResources")
        println(tb.key("header_projectPage").format(webPage))
        println(tb.key("header_projectRepo").format(webRepo))
        println()

        tb.println("about_license")
        println()

        tb.println("about_thirdPartyResources")
        println(
            """Cryptographic algorithms:
Bouncy Castle
Legion of the Bouncy Castle Inc.
MIT License
https://bouncycastle.org
    """.trimIndent()
        )
        println()
        println(
            """ASCII Logo:
Small by Glenn Chappell 4/93 -- based on Standard
Includes ISO Latin-1
Modified by Paul Burton <solution@earthlink.net> 12/96 to include new parameter
supported by FIGlet and FIGWin.
    """.trimMargin()
        )
    }

    fun printHelp(needEg: Boolean = false) {
        fun eg(cmd: String, data: String) = tb.key("help_example").format("${tb.key(cmd)} $data") // for example
        fun eg(cmd: String, id: Int, type: String) =
            tb.key("help_example").format("${tb.key(cmd)} $id ${tb.key(type)}")

        fun eg(cmd: String, id: Int, type: String, data: String) =
            tb.key("help_example").format("${tb.key(cmd)} $id ${tb.key(type)} $data")


        println('\t' + tb.key("help_abbreviated"))
        println()
        println('\t' + tb.key("help_help"))
        println('\t' + tb.key("help_helpWithExamples"))
        println('\t' + tb.key("help_quit"))
        if (!osWindows) println('\t' + tb.key("help_changeLanguage"))
        println('\t' + tb.key("help_license"))

        println()
        tb.println("help_title_files")
        println('\t' + tb.key("help_new"))
        println('\t' + tb.key("help_open").format(if (needEg) eg("c_open2", "file1") else ""))
        println('\t' + tb.key("help_save"))
        println('\t' + tb.key("help_saveAs"))
        println('\t' + tb.key("help_rollback"))

        println()
        tb.println("help_title_table")
        println('\t' + tb.key("help_add"))
        println('\t' + tb.key("help_edit").format(if (needEg) eg("c_edit2", 2, "dt_username2", "admin2") else ""))
        println('\t' + tb.key("help_delete").format(if (needEg) eg("c_delete2", "14") else ""))
        println('\t' + tb.key("help_logPass").format(if (needEg) eg("c_logPass", "1") else ""))
        println('\t' + tb.key("help_show").format(if (needEg) eg("c_show2", "12") else ""))
        println('\t' + tb.key("help_password").format(if (needEg) eg("c_password2", "9") else ""))
        println('\t' + tb.key("help_copy").format(if (needEg) eg("c_copy2", 7, "dt_note2") else ""))
        println('\t' + tb.key("help_search").format(if (needEg) eg("c_search2", "portal") else ""))
        println(
            '\t' + tb.key("help_byTag")
                .format(if (needEg) tb.key("help_example").format("${tb.key("c_byTag2")} ${tb.key("tg_red2")}") else "")
        )
        println('\t' + tb.key("help_table"))

        println()
        tb.println("help_title_abb")
        println('\t' + tb.key("help_dataTypes"))
        println('\t' + tb.key("help_tags"))
    }
}