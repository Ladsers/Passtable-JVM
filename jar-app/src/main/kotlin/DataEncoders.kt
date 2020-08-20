fun tagDecoder(code: String): String{
    return when(code){
        "0" -> "  "
        "1" -> "▌${tb.key("tg_r")}"
        "2" -> "▌${tb.key("tg_g")}"
        "3" -> "▌${tb.key("tg_b")}"
        "4" -> "▌${tb.key("tg_y")}"
        "5" -> "▌${tb.key("tg_p")}"
        else -> "  "
    }
}

fun tagEncoder(tag: String): String{
    return when(tag){
        tb.key("tg_red"), tb.key("tg_r") -> "1"
        tb.key("tg_green"), tb.key("tg_g") -> "2"
        tb.key("tg_blue"), tb.key("tg_b") -> "3"
        tb.key("tg_yellow"), tb.key("tg_y") -> "4"
        tb.key("tg_purple"), tb.key("tg_p") -> "5"
        else -> { println(tb.key("msg_notag")); "0" }
    }
}

fun passEncoder(password: String): String{
    return if (password != "") tb.key("yes")
    else tb.key("no")
}