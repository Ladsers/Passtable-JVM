package com.ladsers.passtable.jvm.processor

import com.ladsers.passtable.jvm.tb

fun tagEncoder(tag: String, showMsg: Boolean = true): String {
    return when (tag) {
        tb.key("tg_red"), tb.key("tg_red2") -> "1"
        tb.key("tg_green"), tb.key("tg_green2") -> "2"
        tb.key("tg_blue"), tb.key("tg_blue2") -> "3"
        tb.key("tg_yellow"), tb.key("tg_yellow2") -> "4"
        tb.key("tg_purple"), tb.key("tg_purple2") -> "5"
        else -> {
            if (showMsg) println(tb.key("msg_entryNoTag"))
            "0"
        }
    }
}