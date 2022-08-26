package com.ladsers.passtable.jvm.printers

import com.ladsers.passtable.jvm.osWindows
import com.ladsers.passtable.jvm.tb
import com.ladsers.passtable.lib.DataItem
import com.ladsers.passtable.lib.DataTable

fun DataTable.print(list: List<DataItem> = getData(), skipUnsaved: Boolean = isSaved, searchMode: Boolean = false) {
    PrintProperties.isTruncated = false
    printTitle()
    for (data in list) if (searchMode) data.printWithOwnId() else data.print(list.indexOf(data) + 1)

    if (list.isEmpty()) println(tb.key("tb_noEntries"))
    if (!skipUnsaved) println(tb.key("msg_unsavedChanges"))
    if (PrintProperties.isTruncated) println(tb.key("msg_showContents"))
}

private fun DataTable.printTitle() {
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

private fun DataItem.print(id: Int) = printer(id)

private fun DataItem.printWithOwnId() = printer(null)

private fun DataItem.printer(id: Int?) {
    println(
        String.format(
            "%-3s | %-3s | %-${PrintProperties.pNote}s | %-${PrintProperties.pUsername}s | %-${PrintProperties.pPassword}s",
            id ?: (this.id + 1),
            tagDecoder(tag),
            note.truncate(PrintProperties.pNote),
            username.truncate(PrintProperties.pUsername),
            passwordEncoder(password)
        )
    )
}

private fun String.truncate(maxLength: Int): String {
    if (this.length > maxLength) {
        PrintProperties.isTruncated = true
        return this.take(maxLength - 1) + "…"
    }
    return this
}

private fun tagDecoder(code: String): String {
    return when (code) {
        "0" -> "  "
        "1" -> "▌${tb.key("tg_red2")}"
        "2" -> "▌${tb.key("tg_green2")}"
        "3" -> "▌${tb.key("tg_blue2")}"
        "4" -> "▌${tb.key("tg_yellow2")}"
        "5" -> "▌${tb.key("tg_purple2")}"
        else -> "  "
    }
}

private fun passwordEncoder(password: String) =
    if (password == "/yes") tb.key("tb_hasPassword") else tb.key("tb_noPassword")
