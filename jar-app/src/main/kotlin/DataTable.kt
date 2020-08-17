import java.io.File

class DataCell(var tag: String, var note: String, var login: String, var password: String)

class DataTable(private var path: String, private var masterPass: String, private val data: String){
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

    fun add(tag: String, note: String, login: String, password: String){
        dataList.add(DataCell(tag,note,login,password))
    }

    fun delete(id: Int){
        dataList.removeAt(id)
    }

    fun setData(id: Int, key: String, new: String){
        when(key){
            "t" -> dataList[id].tag = new
            "n" -> dataList[id].note = new
            "l" -> dataList[id].login = new
            "p" -> dataList[id].password = new
        }
    }

    fun getData(id: Int, key: String): String{
        return when(key){
            "n" -> dataList[id].note
            "l" -> dataList[id].login
            "p" -> dataList[id].password
            else -> ""
        }
    }

    fun save(newPath: String = path, newMasterPass: String = masterPass){
        path = newPath
        masterPass = newMasterPass
        var res = ""
        for (data in dataList) res+= data.tag + "\t" + data.note + "\t" + data.login + "\t" +
                data.password + "\n"
        val encrypt = AesEncryptor.Encryption(res.dropLast(1), masterPass)
        File(path).writeText(CurrentVersionFileA.char() + encrypt) //most likely on Android it will not work like that
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
}