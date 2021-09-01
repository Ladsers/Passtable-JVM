fun printHeader(){
    val webRepo = """https://github.com/Ladsers/Passtable-JVM"""
    val webPage = """https://www.ladsers.com/Passtable"""

    println("""
  ___  _   ___ ___ _____ _   ___ _    ___ 
 | _ \/_\ / __/ __|_   _/_\ | _ ) |  | __|
 |  _/ _ \\__ \__ \ | |/ _ \| _ \ |__| _| 
 |_|/_/ \_\___/___/ |_/_/ \_\___/____|___|
    """.trimIndent())
    println()
    println("Passtable\t${tb.key("header_version")}$version")
    println("${tb.key("header_projectpage")}$webPage")
    println("${tb.key("header_projectrepo")}$webRepo")
    println()
    println(tb.key("header_h"))
    if (!osWindows) println(tb.key("header_ru"))
    println(tb.key("header_license"))

    /* Show a message to users of OS "Windows". */
    if (osWindows) println("\n${tb.key("msg_windows")}")
}