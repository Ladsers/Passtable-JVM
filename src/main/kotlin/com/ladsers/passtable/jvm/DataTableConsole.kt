package com.ladsers.passtable.jvm

import com.ladsers.passtable.lib.DataTable
import java.io.File

class DataTableConsole(path: String? = null, primaryPassword: String? = null, cryptData: String = " "):
    DataTable(path, primaryPassword, cryptData){
    override fun writeToFile(pathToFile: String, cryptData: String) {
        File(pathToFile).writeText(cryptData)
    }
}