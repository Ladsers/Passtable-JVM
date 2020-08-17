fun DataTable.print(){
    println("\n\"${title}\"\n")
    var i=0
    for (data in dataList){
        data.print(dataList.indexOf(data)+1)
        i++
    }
    if (i==0) println(tb.key("msg_noentries"))
}

fun DataTable.printPassword(id: Int){
    println(dataList[id].password)
}

fun DataTable.printSearchByData(trigger: String){
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
    var i=0
    for (data in dataList){
        if (data.tag.contains(trigger)) {
            data.print(dataList.indexOf(data) + 1)
            i++
        }
    }
    if (i==0) println(tb.key("msg_noentries"))
}

fun DataCell.print(id: Int){
    println("$id\t${tagDecoder(tag)}\t$note\t\t\t\t\t\t\t$login\t\t\t\t\t${passEncoder(password)}")
}