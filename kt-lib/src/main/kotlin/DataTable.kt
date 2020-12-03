<<<<<<< HEAD
class DataCell(var tag: String, var note: String, var login: String, var password: String)
=======
/**
 * A class for storing data of one item.
 *
 * Can store the following data: [tag], [note], [login], [password].
 * Additionally, can store a real [id] when searching.
 * @constructor Initialization with all necessary data is required. Data can be an empty string.
 */
class DataItem(var tag: String, var note: String, var login: String, var password: String, val id: Int = -1)
>>>>>>> 67d1070... Fixed bugs in the search.

abstract class DataTable(private var path: String? = null,
                         private var masterPass: String? = null, private val cryptData: String = " "){
<<<<<<< HEAD
    val dataList = mutableListOf<DataCell>()
=======
    /**
     * The main collection containing all items (all user data).
     *
     * @see DataItem
     */
    private val dataList = mutableListOf<DataItem>()

    /**
     * The last saved collection of items.
     *
     * @see DataItem
     */
    private val dataListLastSave = mutableListOf<DataItem>()

    /**
     * Mask a password.
     *
     * @return [/yes] - item has a password, [/no] - item has no password.
     * @see DataItem
     */
    private fun hasPassword(dataItem: DataItem) = if (dataItem.password.isNotEmpty()) "/yes" else "/no"

    /**
     * Save flag.
     *
     * It is checked when user safely close the app or open an another file.
     * It is set every time when user interact with the main collection.
     * If true, saving is not required.
     * @see dataList
     */
>>>>>>> 67d1070... Fixed bugs in the search.
    var isSaved = true
        get() = field
    //init { open() } //??

    /**
<<<<<<< HEAD
     * Add an item to collection.
     */
    fun add(tag: String, note: String, login: String, password: String){
        dataList.add(DataCell(tag,note,login,password))
=======
     * Add an item to the main collection.
     *
     * @return [0] - success, [1] - note is empty and/or login & password is empty.
     * @see dataList
     */
    fun add(tag: String, note: String, login: String, password: String): Int {
        if (note.isEmpty() && (login.isEmpty() || password.isEmpty())) return 1;

        dataList.add(DataItem(tag,note,login,password))
>>>>>>> c65153b... Added protection of empty entry creation. Removed case sensitivity in search.
        isSaved = false
        return 0;
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
<<<<<<< HEAD
     * Getting data from collection.
=======
     * Get all items from the main collection.
     *
     * @return the collection containing all user information except passwords (passwords are hidden).
     * "/yes" - item has a password, "/no" - item has no password.
     * @see dataList
     */
    fun getData(): List<DataItem> {
        val results = mutableListOf<DataItem>()
        for (data in dataList){
            results.add(DataItem(data.tag, data.note, data.login, hasPassword(data)))
        }

        return results
    }

    /**
     * Get data from one item found by [id].
     *
     * [key]: t -> tag, n -> note, l -> login, p -> password.
>>>>>>> 67d1070... Fixed bugs in the search.
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

<<<<<<< HEAD
    fun getPath() = path
=======
    /**
     * Get collection where notes and/or logins contain the following search [query].
     *
     * @return the filtered collection.
     * @see dataList
     */
    fun searchByData(query: String): List<DataItem> {
        val results = mutableListOf<DataItem>()
        val queryLowerCase = query.toLowerCase()
        for ((id, data) in dataList.withIndex()){
            if (data.note.toLowerCase().contains(queryLowerCase) || data.login.toLowerCase().contains(queryLowerCase))
                    results.add(DataItem(data.tag, data.note, data.login, hasPassword(data), id))
        }

        return results
    }

    /**
     * Get collection where tag contain the following search [query].
     *
     * @return the filtered collection.
     * @see dataList
     */
    fun searchByTag(query: String): List<DataItem> {
        val results = mutableListOf<DataItem>()
        for ((id, data) in dataList.withIndex()){
            if (data.tag.contains(query)) results.add(DataItem(data.tag, data.note, data.login, hasPassword(data), id))
        }

        return results
    }

    /**
     * Decrypt and parse data from [cryptData].
     *
     * @return [0] – success, [2] – unsupported file version, [3] – invalid password,
     * [-2] – file is corrupted / unhandled exception
     * @see AesObj
     */
    fun open(): Int {
        dataList.clear()
        if (!masterPass.isNullOrEmpty()) {
            /* Checking the file version. */
            when(cryptData[0]){
                FileVersion.VER_2_TYPE_A.char() -> {
                    try {
                        /* Decrypting data. */
                        val data = AesObj.decrypt(cryptData.removeRange(0, 1), masterPass!!)
                        if (data == "/error") return 3
                        /* Parsing data by template: tag \t note \t login \t password \n. */
                        for (list in data.split("\n")) {
                            val strs = list.split("\t")
                            dataList.add(DataItem(strs[0], strs[1], strs[2], strs[3]))
                        }
                    }
                    catch (e:Exception){
                        return -2
                    }
                }
                else -> return 2
            }
        }
        isSaved = true
        return 0
    }
>>>>>>> c65153b... Added protection of empty entry creation. Removed case sensitivity in search.

    /**
     * Encrypt and save data to the file
     * @return [0] – success, [2] – the saved data does not match the current data,
     * [-2] – encryption error, [3] – saved in the same directory as the app,
     * [4] – empty data, [-3] – error writing to file.
     */
    fun save(newPath: String? = path, newMasterPass: String? = masterPass): Int{
        if (dataList.isEmpty()) return 4
        path = newPath ?: askPath()
        masterPass = newMasterPass ?: askPassword()
        var res = ""
        for (data in dataList) res+= data.tag + "\t" + data.note + "\t" + data.login + "\t" +
                data.password + "\n"
        res = res.dropLast(1)
        var strToSave = CurrentVersionFileA.char().toString()

        try {
            val encrypt =AesEncryptor.Encryption(res, masterPass!!)
            val decrypt = AesEncryptor.Decryption(encrypt, masterPass!!)
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
                path = originalName
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
     * @return [0] – success, [2] – unsupported file version, [3] – invalid password,
     * [-2] – file is corrupted / unhandled exception
     */
    fun open(): Int{
        dataList.clear()
        if (!masterPass.isNullOrEmpty()) {
            when(cryptData[0]){
                FileVersion.VER_2_TYPE_A.char() -> {
                    try {
                        val data = AesEncryptor.Decryption(cryptData.removeRange(0, 1), masterPass!!)
                        if (data == "/error") return 3
                        for (list in data.split("\n")) {
                            val strs = list.split("\t")
                            dataList.add(DataCell(strs[0], strs[1], strs[2], strs[3]))
                        }
                    }
                    catch (e:Exception){
                        return -2
                    }
                }
                else -> return 2
            }
        }
        isSaved = true
        return 0
    }

    abstract fun askPassword() : String
    abstract fun askPath() : String
    abstract fun writeToFile(pathToFile: String, cryptData: String)


}