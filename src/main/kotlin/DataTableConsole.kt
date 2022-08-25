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
        print(tb.key(if (!forSaving) "edit_primaryPassword" else "edit_primaryPasswordNew"))
        val passwordRead = System.console()?.readPassword() ?: readLine()
        val password = if (passwordRead is CharArray) String(passwordRead) else passwordRead.toString()
        when (Verifier.verifyPrimary(password)) {
            0 -> if (!forSaving) return password
            1 -> {
                println(tb.key("mag_primaryEmpty"))
                continue
            }

            2 -> {
                println(tb.key("msg_primaryInvalidChars").format(Verifier.getPrimaryAllowedChars(tb.key("key_space"))))
                continue
            }

            3 -> {
                println(tb.key("msg_primaryForwardSlash"))
                continue
            }

            4 -> {
                println(tb.key("msg_primaryLong"))
                continue
            }
        }

        print(tb.key("edit_primaryPasswordConfirm"))
        val confirmRead = System.console()?.readPassword() ?: readLine()
        val confirm = if (confirmRead is CharArray) String(confirmRead) else confirmRead.toString()
        if (confirm.isNotEmpty() && password != confirm) {
            println(tb.key("msg_passwordsDoNotMatch"))
            continue
        }
        return password
    }
}

fun askFilePath(): String {
    while (true){
        print(tb.key("edit_fileName"))
        val path = fixPath(readLine()!!)
        val delimiter = if (osWindows) "\\" else "/"
        val nameOfFile = path.substringAfterLast(delimiter).substringBeforeLast(".")
        when (Verifier.verifyFileName(nameOfFile)) {
            0 -> return path
            1 -> println(tb.key("msg_nameEmpty"))
            2 -> println(tb.key("msg_nameInvalidChars").format(Verifier.fileNameInvalidChars))
            3 -> println(tb.key("msg_nameSpaceChar"))
            4 -> println(tb.key("msg_nameInvalidWord").format(Verifier.fileNameInvalidWinWords))
            5 -> println(tb.key("msg_nameLong"))
        }
    }
}

object PrintProperties{
    /* Paddings */
    const val pNote = 30
    const val pUsername = 23
    const val pPassword = 8

    var isTruncated = false
}

fun DataTable.print(list: List<DataItem> = getData(), skipUnsaved : Boolean = isSaved, searchMode : Boolean = false) {
    PrintProperties.isTruncated = false
    printTitle()
    for (data in list) if (searchMode) data.printWithOwnId() else data.print(list.indexOf(data) + 1)

    if (list.isEmpty()) println(tb.key("tb_noEntries"))
    if (!skipUnsaved) println(tb.key("msg_unsavedChanges"))
    if (PrintProperties.isTruncated) println(tb.key("msg_showContents"))
}

fun DataTable.printTitle() {
    val delimiter = if (osWindows) "\\" else "/"
    val title = getPath()?.substringAfterLast(delimiter)?.substringBeforeLast(".")
        ?: tb.key("tb_defaultTitle")
    println("\n## $title ##")
    println(
        String.format(
            "%-3s | %-3s | %-${PrintProperties.pNote}s | %-${PrintProperties.pUsername}s | %-${PrintProperties.pPassword}s",
            tb.key("title_id"), tb.key("title_tag"), tb.key("title_note"),
            tb.key("title_username"), tb.key("title_password")
        )
    )
    println(
        String.format(
            "%-3s | %-3s | %-${PrintProperties.pNote}s | %-${PrintProperties.pUsername}s | %-${PrintProperties.pPassword}s",
            "", "", "", "", ""
        ).replace(" ", "-")
    )
}

fun DataItem.print(id: Int) = printer(id)

fun DataItem.printWithOwnId() = printer(null)

private fun DataItem.printer(id: Int?) {
    println(
        String.format(
            "%-3s | %-3s | %-${PrintProperties.pNote}s | %-${PrintProperties.pUsername}s | %-${PrintProperties.pPassword}s",
            id ?: (this.id + 1),
            tagDecoder(tag),
            note.truncate(PrintProperties.pNote),
            username.truncate(PrintProperties.pUsername),
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
    return if (password == "/yes") tb.key("tb_hasPassword")
    else tb.key("tb_noPassword")
}

fun fixPath(path: String): String {
    var correctedPath = path.trim()
    if (correctedPath.startsWith("\"") && correctedPath.endsWith("\"")) correctedPath =
        correctedPath.substring(1, correctedPath.lastIndex)
    if (!correctedPath.endsWith(".passtable")) correctedPath += ".passtable"
    return correctedPath
}