class DataCell(var tag: String, var note: String, var login: String, var password: String)

class DataTable(private var path: String? = null,
                private var masterPass: String? = null, private val cryptData: String = " "){
    val dataList = mutableListOf<DataCell>()
    var isSaved = true
        get() = field
    //init { open() } //??

    /**
     * Add an item to collection.
     */
    fun add(tag: String, note: String, login: String, password: String){
        dataList.add(DataCell(tag,note,login,password))
        isSaved = false
    }

    /**
     * Remove an item from the collection by [id].
     * @return [0] – success, [-2] – IndexOutOfBoundsException, [-1] – unhandled exception.
     */
    fun delete(id: Int): Int{
        try {
            dataList.removeAt(id)
            isSaved = false
        }
        catch (e: Exception){
            return when(e){
                is IndexOutOfBoundsException -> -2
                else -> -1
            }
        }
        return 0
    }

    /**
     * Writing new data instead of the old.
     * @return [0] – success, [1] – wrong key, [-2] – IndexOutOfBoundsException,
     * [-1] – unhandled exception.
     */
    fun setData(id: Int, key: String, new: String): Int{
        try {
            when (key) {
                "t" -> dataList[id].tag = new
                "n" -> dataList[id].note = new
                "l" -> dataList[id].login = new
                "p" -> dataList[id].password = new
                else -> return 1
            }
            isSaved = false
        }
        catch (e:Exception){
            return when(e){
                is IndexOutOfBoundsException -> -2
                else -> -1
            }
        }
        return 0
    }

    /**
     * Getting data from collection.
     * @return [value] – success, [] – wrong key, [/error: outOfBounds] – IndexOutOfBoundsException,
     * [/error: unhandledException] – unhandled exception.
     */
    fun getData(id: Int, key: String): String{
        return try {
            when (key) {
                "n" -> dataList[id].note
                "l" -> dataList[id].login
                "p" -> dataList[id].password
                else -> ""
            }
        }
        catch (e: Exception){
            when(e){
                is IndexOutOfBoundsException -> "/error: outOfBounds"
                else -> "/error: unhandledException"
            }
        }
    }

    fun getPath() = path

    /**
     * Encrypt and save data to the file
     * @return [0] – success, [2] – the saved data does not match the current data,
     * [-2] – encryption error, [3] – saved in the same directory as the app,
     * [-3] – error writing to file.
     */
    fun save(newPath: String? = path, newMasterPass: String? = masterPass): Int{
        path = newPath ?: askPath()
        masterPass = newMasterPass ?: askPassword()
        var res = ""
        for (data in dataList) res+= data.tag + "\t" + data.note + "\t" + data.login + "\t" +
                data.password + "\n"
        res = res.dropLast(1)
        var strToSave = CurrentVersionFileA.char().toString()

        try {
            val encrypt =AesEncryptor.Encryption(res, masterPass)
            val decrypt = AesEncryptor.Decryption(encrypt, masterPass)
            if (decrypt == res) strToSave+= encrypt
            else return 2
        }
        catch (e:Exception){
            return -2
        }

        try {
            writeToFile(path!!, strToSave)
        }
        catch (e:Exception){
            try {
                val originalName = path!!.substringAfterLast("\\").
                substringBeforeLast(".").plus(".passtable")
                writeToFile(originalName, strToSave)
                return 3
            }
            catch (e: Exception){
                return -3
            }
        }
        isSaved = true
        return 0
    }

    /**
     * Decrypt and parse data from a file
     * @return [0] – success, [2] – unsupported file version, [-1] – unhandled exception
     */
    fun open(): Int{
        dataList.clear()
        if (!masterPass.isNullOrEmpty()) {
            when(cryptData[0]){
                FileVersion.VER_2_TYPE_A.char() -> {
                    try {
                        val data = AesEncryptor.Decryption(cryptData.removeRange(0, 1), masterPass)
                        //!! if invalid password -> /error
                        for (list in data.split("\n")) {
                            val strs = list.split("\t")
                            dataList.add(DataCell(strs[0], strs[1], strs[2], strs[3]))
                        }
                    }
                    catch (e:Exception){
                        return -1
                    }
                }
                else -> return 2
            }
        }
        isSaved = true
        return 0
    }
}