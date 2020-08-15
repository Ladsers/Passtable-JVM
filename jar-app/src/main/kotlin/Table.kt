class DataCell(var tag: String, var note: String,
               var login: String, var password: String){
    fun print(id: Int){
        println("$id\t${tagDecoder(tag)}\t$note\t\t\t\t\t\t\t$login\t\t\t\t\t${passEncoder(password)}")
    }
}

fun tagDecoder(code: String): String{
    return when(code){
        "0" -> "  "
        "1" -> "■${tb.key("tg_r")}"
        "2" -> "■${tb.key("tg_g")}"
        "3" -> "■${tb.key("tg_b")}"
        "4" -> "■${tb.key("tg_y")}"
        "5" -> "■${tb.key("tg_p")}"
        else -> "  "
    }
}

fun tagEncoder(tag: String): String{
    return when(tag){
        tb.key("tg_red"), tb.key("tg_r") -> "1"
        tb.key("tg_green"), tb.key("tg_g") -> "2"
        tb.key("tg_blue"), tb.key("tg_b") -> "3"
        tb.key("tg_yellow"), tb.key("tg_y") -> "4"
        tb.key("tg_purple"), tb.key("tg_p") -> "5"
        else -> "0"
    }
}

fun passEncoder(password: String): String{
    return if (password != "") "yes"
    else "no"
}

class Table(private val path: String, private val masterPass: String, private val data: String){
    val title = path.substringAfterLast("\\").substringBeforeLast(".")
    val dataList = mutableListOf<DataCell>()
    init {
        if (data.length>1) {
            for (list in data.split("\n")) {
                val strs = list.split("\t")
                dataList.add(DataCell(strs[0], strs[1], strs[2], strs[3]))
            }
        }
    }

    fun rollback(){
        dataList.clear()
        if (data.length>1) {
            for (list in data.split("\n")) {
                val strs = list.split("\t")
                dataList.add(DataCell(strs[0], strs[1], strs[2], strs[3]))
            }
        }
    }

    fun add(tag: String, note: String, login: String, password: String){
        dataList.add(DataCell(tag,note,login,password))
    }

    fun delete(id: Int){
        dataList.removeAt(id)
    }

    fun print(){
        println("\n\"${title}\"\n")
        var i=0
        for (data in dataList){
            data.print(dataList.indexOf(data)+1)
            i++
        }
        if (i==0) println(tb.key("msg_noentries"))
    }

    fun printSearchByData(trigger: String){
        var i=0
        for (data in dataList){
            if (data.note.contains(trigger) || data.login.contains(trigger)) {
                data.print(dataList.indexOf(data) + 1)
                i++
            }
        }
        if (i==0) println(tb.key("msg_noentries"))
    }

    fun printSearchByTag(trigger: String){
        var i=0
        for (data in dataList){
            if (data.tag.contains(trigger)) {
                data.print(dataList.indexOf(data) + 1)
                i++
            }
        }
        if (i==0) println(tb.key("msg_noentries"))
    }

    fun printPassword(id: Int){
        println(dataList[id].password)
    }

    fun setData(id: Int, key: String, new: String){
        when(key){
            "t" -> dataList[id-1].tag = new //?
            "n" -> dataList[id-1].note = new
            "l" -> dataList[id-1].login = new
            "p" -> dataList[id-1].password = new
        }
    }

    fun getData(id: Int, key: String): String{
        return when(key){
            "n" -> dataList[id-1].note
            "l" -> dataList[id-1].login
            "p" -> dataList[id-1].password
            else -> ""
        }
    }

    fun getString(): String{
        var res = ""
        for (data in dataList) res+= data.tag + "\t" + data.note + "\t" + data.login + "\t" +
                data.password + "\n"
        return res.dropLast(1)
    }
}