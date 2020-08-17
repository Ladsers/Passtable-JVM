import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.io.File


class Processor {
    companion object {
        private var table: DataTable? = null

        fun main(){
            while (true){
                val strs = readLine()?.split(" ") ?: continue
                val send = if (strs.size>1) strs.subList(1,strs.size)
                else listOf("/error")
                when (strs[0].decapitalize()){
                    tb.key("c_help"), "h", "/h", "commands" -> printCommandsList()
                    tb.key("c_heg") -> printCommandsList(true)
                    tb.key("c_en") -> en()
                    tb.key("c_ru") -> ru()

                    tb.key("c_new") -> new()
                    tb.key("c_open"), tb.key("c_op") -> open(send)
                    tb.key("c_save"), tb.key("c_sv") -> save()
                    tb.key("c_saveas") -> saveAs()
                    tb.key("c_rollback") -> rollBack()

                    tb.key("c_add"), tb.key("c_add2") -> add()
                    tb.key("c_edit"), tb.key("c_ed") -> edit(send)
                    tb.key("c_delete"), tb.key("c_del") -> delete(send[0])
                    tb.key("c_copy"), tb.key("c_cp") -> copy(send)
                    tb.key("c_showpassword"), tb.key("c_shp") -> showpassword(send[0])
                    tb.key("c_search"), tb.key("c_s") -> search(send)
                    tb.key("c_bytag"), tb.key("c_bt") -> bytag(send[0])
                    tb.key("c_table"), tb.key("c_t") -> showtable()
                    tb.key("c_quit"), tb.key("c_q") -> return
                    else -> default()
                }
                println()
            }
        }

        private fun showtable() {
            if (table== null) {println(tb.key("msg_notable")); return}
            table!!.print()
        }

        private fun bytag(tag: String) {
            if (tag == ""){
                println()
                return
            }
            table!!.printSearchByTag(tagEncoder(tag))
        }

        private fun search(data: List<String>) {
            val trigger = data.joinToString(separator = " ")
            if (trigger == ""){
                println()
                return
            }
            table!!.printSearchByData(trigger)
        }

        private fun showpassword(id: String) {
            if (table== null) {println(tb.key("msg_notable")); return}
            table!!.printPassword(id.toInt()-1)
        }

        private fun copy(command: List<String>) {
            if (table== null) {println(tb.key("msg_notable")); return}
            val id = command[0].toInt() - 1
            val str = when (command[1]){
                tb.key("dt_note"), tb.key("dt_n") -> table!!.getData(id,"n")
                tb.key("dt_login"), tb.key("dt_l") -> table!!.getData(id,"l")
                tb.key("dt_password"), tb.key("dt_p") -> table!!.getData(id,"p")
                else -> ""
            }

            val selection = StringSelection(str)
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(selection, selection)
        }

        private fun delete(id: String) {
            if (table== null) {println(tb.key("msg_notable")); return}
            table!!.delete(id.toInt()-1)
            table!!.print()
        }

        private fun edit(command: List<String>) {
            if (table== null) {println(tb.key("msg_notable")); return}
            val id = command[0].toInt() - 1
            val data = if (command.size>2) command.subList(2,command.size).joinToString(" ")
            else ""
            when (command[1]){
                tb.key("dt_note"), tb.key("dt_n") -> table!!.setData(id,"n", data)
                tb.key("dt_login"), tb.key("dt_l") -> table!!.setData(id,"l", data)
                tb.key("dt_password"), tb.key("dt_p") -> table!!.setData(id,"p", data)
                tb.key("dt_tag"), tb.key("dt_t") -> table!!.setData(id,"t", tagEncoder(data))
            }
            table!!.print()
        }

        private fun add() {
            if (table== null) {println(tb.key("msg_notable")); return}
            print(tb.key("edit_note"))
            val note = readLine()
            print(tb.key("edit_login"))
            val login = readLine()
            print(tb.key("edit_password"))
            val password = System.console()?.readPassword() ?: readLine()
            print(tb.key("edit_tag"))
            val tag = tagEncoder(readLine()!!)
            table!!.add(tag, note!!, login!!, password!! as String)
            table!!.print()
        }

        private fun rollBack() {
            if (table== null) {println(tb.key("msg_notable")); return}
            table!!.rollback()
            table!!.print()
        }

        private fun saveAs() {
            if (table== null) {println(tb.key("msg_notable")); return}
            print(tb.key("msg_namefile"))
            var path = readLine()
            if (!path!!.endsWith(".passtable")) path += ".passtable"
            print(tb.key("msg_masterpass"))
            val mp = System.console()?.readPassword() ?: readLine()
            table!!.save(path,mp!! as String)
        }

        private fun save() {
            if (table== null) {println(tb.key("msg_notable")); return}
            table!!.save()
        }

        private fun open(path: List<String>) {
            var filePath = path.joinToString(separator = " ")
            if (!filePath.endsWith(".passtable")) filePath += ".passtable"
            print(tb.key("msg_masterpass"))
            val mp = System.console()?.readPassword() ?: readLine()
            var cryptData = File(filePath).readText()
            when(cryptData[0]){
                FileVersion.VER_2_TYPE_A.char() -> {
                    cryptData = cryptData.removeRange(0,1)
                    val data = AesEncryptor.Decryption(cryptData,mp as String)
                    table = DataTable(filePath, mp!! as String,data)
                    if (table==null) throw NullPointerException("Table class was incorrectly initialized")
                    table!!.print()
                }
                else -> TODO("Error: Unsupported version of the file")
            }
        }

        private fun new() {
            print(tb.key("msg_namefile"))
            var name = readLine()
            if (!name!!.endsWith(".passtable")) name += ".passtable"
            print(tb.key("msg_masterpass"))
            val mp = System.console()?.readPassword() ?: readLine()
            table = DataTable(name, mp!! as String,"")
            if (table==null) throw NullPointerException("Table class was incorrectly initialized")
            table!!.print()
        }

        private fun en(){
            tb.changeLocale("en")
            println(tb.key("msg_lang"))
        }

        private fun ru(){
            tb.changeLocale("ru")
            println(tb.key("msg_lang"))
        }

        private fun default(){
            println(tb.key("msg_unknown"))
        }
    }
}