import com.ladsers.passtable.lib.DataItem
import com.ladsers.passtable.lib.DataTable
import com.ladsers.passtable.lib.Verifier
import java.io.File

class DataTableConsole(path: String? = null, primaryPassword: String? = null, cryptData: String = " "):
    DataTable(path, primaryPassword, cryptData){
    override fun writeToFile(pathToFile: String, cryptData: String) {
        File(pathToFile).writeText(cryptData)
    }
}

fun askPrimaryPassword(forSaving: Boolean = false): String {
    while (true) {
        print(tb.key(if (!forSaving) "msg_primarypassword" else "msg_primarypasswordnew"))
        val passwordRead = System.console()?.readPassword() ?: readLine()
        val password = if (passwordRead is CharArray) String(passwordRead) else passwordRead.toString()
        when (Verifier.verifyPrimary(password)) {
            0 -> if (!forSaving) return password
            1 -> {
                println(tb.key("msg_emptyprimarypassword"))
                continue
            }

            2 -> {
                println(tb.key("msg_invalidcharprimarypassword") + '\n' + Verifier.getPrimaryAllowedChars(tb.key("key_space")))
                continue
            }

            3 -> {
                println(tb.key("msg_slashprimarypassword"))
                continue
            }

            4 -> {
                println(tb.key("msg_longprimarypassword"))
                continue
            }
        }

        print(tb.key("msg_confirm"))
        val confirmRead = System.console()?.readPassword() ?: readLine()
        val confirm = if (confirmRead is CharArray) String(confirmRead) else confirmRead.toString()
        if (confirm.isNotEmpty() && password != confirm) {
            println(tb.key("msg_dontmatch"))
            continue
        }
        return password
    }
}

fun askPathConsole(): String {
    while (true){
        print(tb.key("msg_namefile"))
        val path = fixPath(readLine()!!)
        val delimiter = if (osWindows) "\\" else "/"
        val nameOfFile = path.substringAfterLast(delimiter).substringBeforeLast(".")
        when (Verifier.verifyFileName(nameOfFile)) {
            0 -> return path
            1 -> println(tb.key("msg_emptynamefile"))
            2 -> println(tb.key("msg_charerror") + Verifier.fileNameInvalidChars)
            3 -> println(tb.key("msg_whitespacenamefile"))
            4 -> println(tb.key("msg_invalidwordnamefile") + Verifier.fileNameInvalidWinWords)
            5 -> println(tb.key("msg_longnamefile"))
        }
    }
}

object PrintProperties{
    /* Paddings */
    const val pNote = 30
    const val pLogin = 23
    const val pPassword = 8

    var isTruncated = false
}

fun DataTable.print(list: List<DataItem> = getData(), skipUnsaved : Boolean = isSaved, searchMode : Boolean = false) {
    PrintProperties.isTruncated = false
    printTitle()
    for (data in list) if (searchMode) data.printWithOwnId() else data.print(list.indexOf(data) + 1)

    if (list.isEmpty()) println(tb.key("msg_noentries"))
    if (!skipUnsaved) println(tb.key("msg_unsaved"))
    if (PrintProperties.isTruncated) println(tb.key("msg_showcontents"))
}

fun DataTable.printTitle() {
    val title = getPath()?.substringAfterLast("\\")?.substringBeforeLast(".")
        ?: tb.key("tb_defaulttitle")
    println("\n## $title ##")
    println(
        String.format(
            "%-3s | %-3s | %-${PrintProperties.pNote}s | %-${PrintProperties.pLogin}s | %-${PrintProperties.pPassword}s",
            tb.key("title_id"), tb.key("title_tag"), tb.key("title_note"),
            tb.key("title_login"), tb.key("title_password")
        )
    )
    println(
        String.format(
            "%-3s | %-3s | %-${PrintProperties.pNote}s | %-${PrintProperties.pLogin}s | %-${PrintProperties.pPassword}s",
            "", "", "", "", ""
        ).replace(" ", "-")
    )
}

fun DataItem.print(id: Int) = printer(id)

fun DataItem.printWithOwnId() = printer(null)

private fun DataItem.printer(id: Int?) {
    println(
        String.format(
            "%-3s | %-3s | %-${PrintProperties.pNote}s | %-${PrintProperties.pLogin}s | %-${PrintProperties.pPassword}s",
            id ?: this.id + 1,
            tagDecoder(tag),
            note.truncate(PrintProperties.pNote),
            username.truncate(PrintProperties.pLogin),
            passEncoder(password)
        )
    )
}

fun String.truncate(maxLength: Int): String{
    if (this.length > maxLength) {
        PrintProperties.isTruncated = true
        return this.take(maxLength - 1) + "…"
    }
    return this
}

fun passEncoder(password: String): String{
    return if (password == "/yes") tb.key("yes")
    else tb.key("no")
}

fun fixPath(path: String): String {
    var correctedPath = path
    if (path.startsWith("\"") && path.endsWith("\"")) correctedPath = path.substring(1, path.lastIndex)
    if (!correctedPath.endsWith(".passtable")) correctedPath += ".passtable"
    return correctedPath
}