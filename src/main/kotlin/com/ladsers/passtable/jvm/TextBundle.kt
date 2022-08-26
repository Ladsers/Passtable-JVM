package com.ladsers.passtable.jvm

import java.util.*

class TextBundle {
    private var rb = ResourceBundle.getBundle("text", Locale.ROOT)
    private val jre8 = System.getProperty("java.version").startsWith("1.8.")

    fun changeLocale(language: String) {
        val locale: Locale = when (language) {
            "ru" -> Locale("ru", "RU")
            else -> Locale.ROOT
        }
        rb = ResourceBundle.getBundle("text", locale)
    }

    fun key(key: String): String {
        return if (jre8) String(rb.getString(key).toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
        else rb.getString(key)
    }

    fun print(key: String) {
        kotlin.io.print(key(key))
    }

    fun println(key: String) {
        kotlin.io.println(key(key))
    }
}