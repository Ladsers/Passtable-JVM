fun printCommandsList(needEg: Boolean = false){
    println(tb.key("help_h"))
    println(tb.key("help_heg"))
    println(tb.key("help_language"))

    println()
    println(tb.key("help_title_files"))
    println(tb.key("help_new"))
    if (needEg) println(tb.key("help_op_eg")) else println(tb.key("help_op"))
    println(tb.key("help_sv"))
    println(tb.key("help_saveas"))
    println(tb.key("help_rollback"))

    println()
    println(tb.key("help_title_table"))
    println(tb.key("help_add"))
    if (needEg) println(tb.key("help_ed_eg")) else println(tb.key("help_ed"))
    if (needEg) println(tb.key("help_del_eg")) else println(tb.key("help_del"))
    if (needEg) println(tb.key("help_cp_eg")) else println(tb.key("help_cp"))
    if (needEg) println(tb.key("help_shp_eg")) else println(tb.key("help_shp"))
    if (needEg) println(tb.key("help_s_eg")) else println(tb.key("help_s"))
    if (needEg) println(tb.key("help_bt_eg")) else println(tb.key("help_bt"))
    println(tb.key("help_t"))

    println()
    println(tb.key("help_title_abb"))
    println(tb.key("help_datatypes"))
    println(tb.key("help_tags"))

    println()
}