import java.io.File

class DataTableConsole(path: String? = null, masterPass: String? = null, cryptData: String = " "):
    DataTable(path, masterPass, cryptData){
    override fun askPassword(): String {
        return askPasswordConsole()
    }

    override fun askPath(): String {
        return askPathConsole()
    }

    override fun writeToFile(pathToFile: String, cryptData: String) {
        File(pathToFile).writeText(cryptData)
    }
}

fun askPasswordConsole(): String {
    var pass: String
    while (true) {
        print(tb.key("msg_masterpass"))
        val mp = System.console()?.readPassword()?:readLine()
        pass = if (mp is CharArray) String(mp) else mp.toString()
        if (pass.isEmpty()) { println(tb.key("msg_emptymasterpass")); continue }
        if (pass.contains('\t')) { println(tb.key("msg_tabchar")); continue }
        if (pass.length != pass.toByteArray().size) { println(tb.key("msg_nonlatin")); continue }
        if (pass.startsWith("/")) { println(tb.key("msg_slashmasterpass")); continue }
        break
    }
    return pass
}

fun askPathConsole(): String {
    var path: String
    while (true){
        print(tb.key("msg_namefile"))
        path = readLine()!!
        if (path.isEmpty()) { println(tb.key("msg_emptynamefile")); continue }
        if (path.contains('\t')) { println(tb.key("msg_tabchar")); continue }
        val nameOfFile = path.substringAfterLast("\\").substringBeforeLast(".")
        val chars = charArrayOf(':','*','?','\"', '<', '>', '|')
        var i = 0
        for (ch in chars) {
            if (nameOfFile.contains(ch)) { println(tb.key("msg_charerror")); i++ }
        }
        if (i>0) continue
        break
    }
    if (!path.endsWith(".passtable")) path += ".passtable"
    return path
}

fun DataTable.print(list: List<DataItem> = getData(), skipUnsaved : Boolean = isSaved) {
    printTitle()
    var i=0
    for (data in list){
        data.print(list.indexOf(data)+1)
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

fun passEncoder(password: String): String{
    return if (password == "/yes") tb.key("yes")
    else tb.key("no")
}