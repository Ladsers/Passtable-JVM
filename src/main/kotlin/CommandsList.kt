fun printCommandsList(needEg: Boolean = false){
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
    println(tb.key("help_title_files"))
    println('\t' + tb.key("help_new"))
    println('\t' + tb.key("help_open").format(if (needEg) eg("c_open2", "file1") else ""))
    println('\t' + tb.key("help_save"))
    println('\t' + tb.key("help_saveAs"))
    println('\t' + tb.key("help_rollback"))

    println()
    println(tb.key("help_title_table"))
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
    println(tb.key("help_title_abb"))
    println('\t' + tb.key("help_dataTypes"))
    println('\t' + tb.key("help_tags"))
}