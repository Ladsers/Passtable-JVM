package com.ladsers.passtable.jvm.printers

import com.ladsers.passtable.jvm.tb

fun tagDecoder(code: String): String{
    return when(code){
        "0" -> "  "
        "1" -> "▌${tb.key("tg_red2")}"
        "2" -> "▌${tb.key("tg_green2")}"
        "3" -> "▌${tb.key("tg_blue2")}"
        "4" -> "▌${tb.key("tg_yellow2")}"
        "5" -> "▌${tb.key("tg_purple2")}"
        else -> "  "
    }
}