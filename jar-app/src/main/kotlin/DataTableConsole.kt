import java.io.File

fun DataTable.print(){
    printTitle()
    var i=0
    for (data in dataList){
        data.print(dataList.indexOf(data)+1)
        i++
    }
    if (i==0) println(tb.key("msg_noentries"))
    if (!isSaved) println(tb.key("msg_unsaved"))
}

/**
 * Printing the password to console by [id].
 * @return [0] – success, [-2] – IndexOutOfBoundsException, [-1] – unhandled exception.
 */
fun DataTable.printPassword(id: Int): Int{
    try {
        println(dataList[id].password)
    }
    catch (e: Exception){
        return when(e){
            is IndexOutOfBoundsException -> -2
            else -> -1
        }
    }
    return 0
}

fun DataTable.printSearchByData(trigger: String){
    printTitle()
    var i=0
    for (data in dataList){
        if (data.note.contains(trigger) || data.login.contains(trigger)) {
            data.print(dataList.indexOf(data) + 1)
            i++
        }
    }
    if (i==0) println(tb.key("msg_noentries"))
}

fun DataTable.printSearchByTag(trigger: String){
    printTitle()
    var i=0
    for (data in dataList){
        if (data.tag.contains(trigger)) {
            data.print(dataList.indexOf(data) + 1)
            i++
        }
    }
    if (i==0) println(tb.key("msg_noentries"))
}

private fun DataTable.printTitle(){
    val title = getPath()?.substringAfterLast("\\")?.substringBeforeLast(".")
            ?: tb.key("tb_defaulttitle")
    println("\n## ${title} ##")
    println(String.format("%-3s | %-3s | %-35s | %-23s | %-8s",
            tb.key("title_id"), tb.key("title_tag"), tb.key("title_note"),
            tb.key("title_login"), tb.key("title_password")))
    println(String.format("%-3s | %-3s | %-35s | %-23s | %-8s",
            "","","","","").replace(" ","-"))
}

fun askPath(): String {
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

fun askPassword(): String {
    var pass: String
    while (true) {
        print(tb.key("msg_masterpass"))
        val mp = System.console()?.readPassword() ?: readLine()
        pass = mp as String
        if (pass.isEmpty()) { println(tb.key("msg_emptymasterpass")); continue }
        if (pass.contains('\t')) { println(tb.key("msg_tabchar")); continue }
        if (pass.length != pass.toByteArray().size) { println(tb.key("msg_nonlatin")); continue }
        if (pass.startsWith("/")) { println(tb.key("msg_slashmasterpass")); continue }
        break
    }
    return pass
}

fun DataTable.writeToFile(pathToFile: String, cryptData: String){
    File(pathToFile).writeText(cryptData)
}

fun DataCell.print(id: Int){
    println(String.format("%-3s | %-3s | %-35s | %-23s | %-8s",
            id, tagDecoder(tag), note, login, passEncoder(password)))
}