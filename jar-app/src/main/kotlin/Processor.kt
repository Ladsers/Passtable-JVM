import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.io.File
import java.lang.Exception


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
                    tb.key("c_bytag"), tb.key("c_bt") -> byTag(send[0])
                    tb.key("c_table"), tb.key("c_t") -> showTable()
                    tb.key("c_quit"), tb.key("c_q") -> if (protectionUnsaved()) return
                    else -> default()
                }
                println()
            }
        }

        fun quickStart(){
            table = DataTable()
            table!!.print()
            println()
        }

        private fun protectionUnsaved(): Boolean {
            if (!table!!.isSaved){
                while (true){
                    println(tb.key("msg_saveornot"))
                    val com = readLine() ?: continue
                    when(com){
                        tb.key("c_yes2") -> { save(); return true } //cancel if error!
                        tb.key("c_no2") -> return true
                        tb.key("c_cancel") -> return false
                        //TODO("canceled message")
                        else -> println(tb.key("msg_unknown"))
                    }
                }
            }
            return true
        }

        private fun showTable() {
            table!!.print()
        }

        private fun byTag(tag: String) {
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
            table!!.open()
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
            table!!.save()
        }

        private fun open(path: List<String>) {
            if (!protectionUnsaved()) return
            var filePath = path.joinToString(separator = " ")
            if (!filePath.endsWith(".passtable")) filePath += ".passtable"
            print(tb.key("msg_masterpass"))
            val cryptData:String
            try {
                cryptData = File(filePath).readText()
            }
            catch (e:Exception){
                //TODO("failed to open the file")
                return
            }
            table = DataTable(filePath, askPassword(), cryptData)
            //TODO("invalid password")?
            if (table != null) {
                when(table!!.open()){
                    0 -> {table!!.print()}
                    2 -> {//TODO("unsupported file version")
                    quickStart()}
                    -1 -> {println(tb.key("msg_exception")); quickStart()}
                }
            } else {
                println(tb.key("msg_incorrectinit"))
                quickStart()
            }
        }

        private fun new() {
            if (!protectionUnsaved()) return
            table = DataTable(path = askPath())
            if (table != null) table!!.print()
            else {
                println(tb.key("msg_incorrectinit"))
                quickStart()
            }
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