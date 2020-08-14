class Processor {
    companion object {
        fun main(){
            while (true){
                val strs = readLine()?.split(" ") ?: continue
                val send = strs.subList(1,strs.lastIndex)
                when (strs[0]){
                    tb.key("c_help"), "h", "/h", "commands" -> printCommandsList()
                    tb.key("c_heg") -> printCommandsList(true)
                    tb.key("c_en") -> en()
                    tb.key("c_ru") -> ru()

                    tb.key("c_new") -> new()
                    tb.key("c_open"), tb.key("c_op") -> open(send[0])
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
                    else -> default()
                }
                println()
            }
        }

        private fun showtable() {
            TODO("Not yet implemented")
        }

        private fun bytag(tag: String) {
            TODO("Not yet implemented")
        }

        private fun search(data: List<String>) {
            TODO("Not yet implemented")
        }

        private fun showpassword(id: String) {
            TODO("Not yet implemented")
        }

        private fun copy(command: List<String>) {
            TODO("Not yet implemented")
        }

        private fun delete(id: String) {
            TODO("Not yet implemented")
        }

        private fun edit(command: List<String>) {
            TODO("Not yet implemented")
        }

        private fun add() {
            TODO("Not yet implemented")
        }

        private fun rollBack() {
            TODO("Not yet implemented")
        }

        private fun saveAs() {
            TODO("Not yet implemented")
        }

        private fun save() {
            TODO("Not yet implemented")
        }

        private fun open(path: String) {
            TODO("Not yet implemented")
        }

        private fun new() {
            TODO("Not yet implemented")
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