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

fun DataTable.printPassword(id: Int){
    println(dataList[id].password)
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

fun DataTable.askPath(): String {
    print(tb.key("msg_namefile"))
    var name = readLine()
    if (!name!!.endsWith(".passtable")) name += ".passtable"
    return name
}

fun DataTable.askPassword(): String {
    print(tb.key("msg_masterpass"))
    val mp = System.console()?.readPassword() ?: readLine()
    return mp as String
}

fun DataTable.writeToFile(pathToFile: String, cryptData: String){
    File(pathToFile).writeText(cryptData)
}

fun DataCell.print(id: Int){
    println(String.format("%-3s | %-3s | %-35s | %-23s | %-8s",
            id, tagDecoder(tag), note, login, passEncoder(password)))
}