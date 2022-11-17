package com.ladsers.passtable.jvm

import com.ladsers.passtable.jvm.printers.InfoPrinter
import com.ladsers.passtable.jvm.processor.Processor
import com.ladsers.passtable.lib.Updater

val tb = TextBundle()
val osWindows = System.getProperty("os.name").startsWith("win", true)
val osMac = System.getProperty("os.name").startsWith("mac", true)
const val version = "22.11.0"

fun main(args: Array<String>) {
    val argList = args.toMutableList()
    if (argList.isNotEmpty() && !osWindows) {
        when (argList[0].lowercase()) {
            "-ru-ru" -> {
                tb.changeLocale("ru")
                argList.removeAt(0)
            }
        }
    }

    InfoPrinter.printHeader()
    Updater.run()

    when {
        argList.size > 1 -> Processor.openProcess(argList[0], argList[1])
        argList.isNotEmpty() -> {
            println()
            Processor.openProcess(argList[0])
        }

        else -> Processor.quickStart()
    }
    println()

    while (true) {
        try {
            Processor.main()
        } catch (e: Exception) {
            println("$e\n")
            continue
        }
        break
    }
}