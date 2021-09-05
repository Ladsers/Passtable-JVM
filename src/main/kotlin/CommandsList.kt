fun printCommandsList(needEg: Boolean = false){
    print('\t'); println(tb.key("help_abbreviated"))
    println()
    print('\t'); println(tb.key("help_h"))
    print('\t'); println(tb.key("help_heg"))
    print('\t'); println(tb.key("help_q"))
    if (!osWindows) {
        print('\t'); println(tb.key("help_language"))
    }
    print('\t'); println(tb.key("help_license"))

    println()
    println(tb.key("help_title_files"))
    print('\t'); println(tb.key("help_new"))
    print('\t'); if (needEg) println(tb.key("help_op_eg")) else println(tb.key("help_op"))
    print('\t'); println(tb.key("help_sv"))
    print('\t'); println(tb.key("help_saveas"))
    print('\t'); println(tb.key("help_rollback"))

    println()
    println(tb.key("help_title_table"))
    print('\t'); println(tb.key("help_add"))
    print('\t'); if (needEg) println(tb.key("help_ed_eg")) else println(tb.key("help_ed"))
    print('\t'); if (needEg) println(tb.key("help_del_eg")) else println(tb.key("help_del"))
    print('\t'); if (needEg) println(tb.key("help_sh_eg")) else println(tb.key("help_sh"))
    print('\t'); if (needEg) println(tb.key("help_ps_eg")) else println(tb.key("help_ps"))
    print('\t'); if (needEg) println(tb.key("help_cp_eg")) else println(tb.key("help_cp"))
    print('\t'); if (needEg) println(tb.key("help_s_eg")) else println(tb.key("help_s"))
    print('\t'); if (needEg) println(tb.key("help_bt_eg")) else println(tb.key("help_bt"))
    print('\t'); println(tb.key("help_t"))

    println()
    println(tb.key("help_title_abb"))
    print('\t'); println(tb.key("help_datatypes"))
    print('\t'); println(tb.key("help_tags"))
}