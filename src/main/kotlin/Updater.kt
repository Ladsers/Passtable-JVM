import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import kotlin.system.exitProcess

// TODO: change /Ladsers/temp-files/ to /Ladsers/Passtable-JVM/

fun Updater.run() {
    println("\n${tb.key("msg_checkingupdates")}")
    when (check("jvm", version)) {
        0 -> println(tb.key("msg_uptodate"))
        -1 -> println(tb.key("msg_failconnect"))
        1 -> {
            println(tb.key("msg_newversion"))
            when (readLine()!!) {
                tb.key("c_yes2") -> download()
                else -> return
            }
        }
    }
}

private fun Updater.download() {
    val urlGitHub = "https://github.com/Ladsers/temp-files/releases/download"
    val newApp = "Passtable-${getLastVer()}.jar"
    val successMsg = "${tb.key("msg_dlsuccess1")} \"$newApp\" ${tb.key("msg_dlsuccess2")}"

    println(tb.key("msg_downloading"))
    val fos = FileOutputStream(newApp)
    try {
        val url = URL("$urlGitHub/${getLastVer()}/$newApp")
        val rbc = Channels.newChannel(url.openStream())
        fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
        rbc.close()
        println(successMsg)
        exitProcess(0)
    } catch (e: Exception) {
        fos.close()
        val tempFile = File(newApp)
        if (tempFile.exists()) tempFile.delete()
        println(tb.key("msg_dlfail"))
    }
}