fun printCommandsList(needEg: Boolean = false){
    fun eg(cmd: String, data: String) = tb.key("help_example").format("${tb.key(cmd)} $data") // for example
    fun eg(cmd: String, id: Int, type: String) =
        tb.key("help_example").format("${tb.key(cmd)} $id ${tb.key(type)}")
    fun eg(cmd: String, id: Int, type: String, data: String) =
        tb.key("help_example").format("${tb.key(cmd)} $id ${tb.key(type)} $data")


    println('\t' + tb.key("help_abbreviated"))
    println()
    println('\t' + tb.key("help_h"))
    println('\t' + tb.key("help_heg"))
    println('\t' + tb.key("help_q"))
    if (!osWindows) println('\t' + tb.key("help_language"))
    println('\t' + tb.key("help_license"))

    println()
    println(tb.key("help_title_files"))
    println('\t' + tb.key("help_new"))
    println('\t' + tb.key("help_op").format(if (needEg) eg("c_op", "file1") else ""))
    println('\t' + tb.key("help_sv"))
    println('\t' + tb.key("help_saveas"))
    println('\t' + tb.key("help_rollback"))

    println()
    println(tb.key("help_title_table"))
    println('\t' + tb.key("help_add"))
    println('\t' + tb.key("help_ed").format(if (needEg) eg("c_ed", 2, "dt_u", "admin2") else ""))
    println('\t' + tb.key("help_del").format(if (needEg) eg("c_del", "14") else ""))
    println('\t' + tb.key("help_lnp").format(if (needEg) eg("c_lnp", "1") else ""))
    println('\t' + tb.key("help_sh").format(if (needEg) eg("c_sh", "12") else ""))
    println('\t' + tb.key("help_ps").format(if (needEg) eg("c_ps", "9") else ""))
    println('\t' + tb.key("help_cp").format(if (needEg) eg("c_cp", 7, "dt_n") else ""))
    println('\t' + tb.key("help_s").format(if (needEg) eg("c_s", "portal") else ""))
    println(
        '\t' + tb.key("help_bt")
            .format(if (needEg) tb.key("help_example").format("${tb.key("c_bt")} ${tb.key("tg_r")}") else "")
    )
    println('\t' + tb.key("help_t"))

    println()
    println(tb.key("help_title_abb"))
    println('\t' + tb.key("help_datatypes"))
    println('\t' + tb.key("help_tags"))
}