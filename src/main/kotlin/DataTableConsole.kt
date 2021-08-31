import java.io.File

class DataTableConsole(path: String? = null, masterPass: String? = null, cryptData: String = " "):
    DataTable(path, masterPass, cryptData){
    override fun writeToFile(pathToFile: String, cryptData: String) {
        File(pathToFile).writeText(cryptData)
    }
}

fun askPasswordConsole(): String {
    while (true) {
        print(tb.key("msg_masterpass"))
        val mp = System.console()?.readPassword() ?: readLine()
        val pass = if (mp is CharArray) String(mp) else mp.toString()
        when (Verifier.verifyMp(pass)) {
            0 -> return pass
            1 -> println(tb.key("msg_emptymasterpass"))
            2 -> println(tb.key("msg_invalidcharmasterpass") + '\n' + Verifier.getMpAllowedChars(tb.key("key_space")))
            3 -> println(tb.key("msg_slashmasterpass"))
            4 -> println(tb.key("msg_longmasterpass"))
        }
    }
}

fun askPathConsole(): String {
    while (true){
        print(tb.key("msg_namefile"))
        var path = readLine()!!
        if (!path.endsWith(".passtable")) path += ".passtable"
        val delimiter = if (System.getProperty("os.name").startsWith("win", true)) "\\" else "/"
        val nameOfFile = path.substringAfterLast(delimiter).substringBeforeLast(".")
        when (Verifier.verifyFileName(nameOfFile)) {
            0 -> return path
            1 -> println(tb.key("msg_emptynamefile"))
            2 -> println(tb.key("msg_charerror") + Verifier.fileNameInvalidChars)
            3 -> println(tb.key("msg_whitespacenamefile"))
            4 -> println(tb.key("msg_invalidwordnamefile") + Verifier.fileNameInvalidWinWords)
            5 -> println(tb.key("msg_longnamefile"))
        }
    }
}

fun DataTable.print(list: List<DataItem> = getData(), skipUnsaved : Boolean = isSaved, searchMode : Boolean = false) {
    printTitle()
    var i=0
    for (data in list){
        if (searchMode) data.printWithKnownId()
        else data.print(list.indexOf(data)+1)
        i++
    }
    if (i==0) println(tb.key("msg_noentries"))
    if (!skipUnsaved) println(tb.key("msg_unsaved"))
}

fun DataTable.printTitle(){
    val title = getPath()?.substringAfterLast("\\")?.substringBeforeLast(".")
            ?: tb.key("tb_defaulttitle")
    println("\n## ${title} ##")
    println(String.format("%-3s | %-3s | %-35s | %-23s | %-8s",
            tb.key("title_id"), tb.key("title_tag"), tb.key("title_note"),
            tb.key("title_login"), tb.key("title_password")))
    println(String.format("%-3s | %-3s | %-35s | %-23s | %-8s",
            "","","","","").replace(" ","-"))
}

fun DataItem.print(id: Int){
    println(String.format("%-3s | %-3s | %-35s | %-23s | %-8s",
            id, tagDecoder(tag), note, login, passEncoder(password)))
}

fun DataItem.printWithKnownId(){
    println(String.format("%-3s | %-3s | %-35s | %-23s | %-8s",
        id+1, tagDecoder(tag), note, login, passEncoder(password)))
}

fun passEncoder(password: String): String{
    return if (password == "/yes") tb.key("yes")
    else tb.key("no")
}