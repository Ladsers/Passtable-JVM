import java.io.File

class DataCell(var tag: String, var note: String, var login: String, var password: String)

class DataTable(private var path: String? = null,
                private var masterPass: String? = null, private val cryptData: String = " "){
    val dataList = mutableListOf<DataCell>()
    var isSaved = true
        get() = field
    init { open() }

    fun add(tag: String, note: String, login: String, password: String){
        dataList.add(DataCell(tag,note,login,password))
        isSaved = false
    }

    fun delete(id: Int){
        dataList.removeAt(id)
        isSaved = false
    }

    fun setData(id: Int, key: String, new: String){
        when(key){
            "t" -> dataList[id].tag = new
            "n" -> dataList[id].note = new
            "l" -> dataList[id].login = new
            "p" -> dataList[id].password = new
            else -> return
        }
        isSaved = false
    }

    fun getData(id: Int, key: String): String{
        return when(key){
            "n" -> dataList[id].note
            "l" -> dataList[id].login
            "p" -> dataList[id].password
            else -> ""
        }
    }

    fun getPath() = path

    fun save(newPath: String? = path, newMasterPass: String? = masterPass){
        path = newPath ?: askPath()
        masterPass = newMasterPass ?: askPassword()
        var res = ""
        for (data in dataList) res+= data.tag + "\t" + data.note + "\t" + data.login + "\t" +
                data.password + "\n"
        val encrypt = CurrentVersionFileA.char()+
                AesEncryptor.Encryption(res.dropLast(1), masterPass)
        writeToFile(path!!, encrypt)
        isSaved = true
    }

    fun open(){
        dataList.clear()
        if (!masterPass.isNullOrEmpty()) {
            when(cryptData[0]){
                FileVersion.VER_2_TYPE_A.char() -> {
                    val data = AesEncryptor.Decryption(cryptData.removeRange(0,1), masterPass)
                    for (list in data.split("\n")) {
                        val strs = list.split("\t")
                        dataList.add(DataCell(strs[0], strs[1], strs[2], strs[3]))
                    }
                }
                else -> TODO("Error: Unsupported version of the file")
            }
        }
        isSaved = true
    }
}