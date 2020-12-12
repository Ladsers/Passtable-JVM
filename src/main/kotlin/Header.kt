fun printHeader(){
    val webRepo = """https://github.com/Ladsers/Passtable"""
    val webPage = """https://www.ladsers.com/Passtable"""

    println("""
  ___  _   ___ ___ _____ _   ___ _    ___ 
 | _ \/_\ / __/ __|_   _/_\ | _ ) |  | __|
 |  _/ _ \\__ \__ \ | |/ _ \| _ \ |__| _| 
 |_|/_/ \_\___/___/ |_/_/ \_\___/____|___|
    """.trimIndent())
    println()
    println("Passtable\tversion: $version")
    println("Project page: $webPage")
    println("Project repo: $webRepo")
    println()
    println("Type \"h\" to display a list of commands.")
    println("Type \"ru\" to change language to Russian (Русский язык).")
    println("Type \"license\" to view the license.")

    /* Show a message to users of OS "Windows". */
    if (System.getProperty("os.name").startsWith("win", true)){
        println()
        println(tb.key("msg_windows"))
    }
}