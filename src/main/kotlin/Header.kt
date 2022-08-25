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
    println("Passtable\t${tb.key("header_version").format(version)}")
    println(tb.key("header_projectPage").format(webPage))
    println(tb.key("header_projectRepo").format(webRepo))
    println()
    println(tb.key("header_h"))
    if (!osWindows) println(tb.key("header_ru"))
    println(tb.key("header_license"))
}