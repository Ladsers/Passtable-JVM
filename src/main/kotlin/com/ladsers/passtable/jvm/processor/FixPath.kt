package com.ladsers.passtable.jvm.processor

fun fixPath(path: String): String {
    var correctedPath = path.trim()
    if (correctedPath.startsWith("\"") && correctedPath.endsWith("\"")) correctedPath =
        correctedPath.substring(1, correctedPath.lastIndex)
    if (!correctedPath.endsWith(".passtable")) correctedPath += ".passtable"
    return correctedPath
}