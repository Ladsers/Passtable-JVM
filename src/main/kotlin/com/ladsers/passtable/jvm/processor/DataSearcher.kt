package com.ladsers.passtable.jvm.processor

import com.ladsers.passtable.jvm.printers.print
import com.ladsers.passtable.jvm.tb
import com.ladsers.passtable.lib.DataTable

object DataSearcher {
    fun search(data: List<String>, table: DataTable) {
        val trigger = data.joinToString(separator = " ")
        if (!DataVerifier.verifySearchQuery(trigger)) return
        table.print(table.searchByData(trigger), true, searchMode = true)
    }

    fun byTag(tag: String, table: DataTable) {
        if (!DataVerifier.verifySearchQuery(tag)) return
        if (tagEncoder(tag, false) != "0") {
            table.print(table.searchByTag(tagEncoder(tag)), true, searchMode = true)
        } else tb.println("msg_invalidTag")
    }
}