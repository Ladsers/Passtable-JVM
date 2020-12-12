import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import kotlin.system.exitProcess

// TODO: change /Ladsers/temp-files/ to /Ladsers/Passtable/

fun Updater.run() {
    println("\nChecking for updates...")
    when (check("jvm", version)) {
        0 -> println("Ok")
        -1 -> println("Failed: could not connect to the server!")
        1 -> {
            println("New version available! Do you want to download? (y/n)")
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
    val successMsg = "Success! Open \"$newApp\" in the same directory to continue working."

    println("Downloading...")
    val fos = FileOutputStream(newApp)
    try{
        val url = URL("$urlGitHub/jvm-${getLastVer()}/$newApp") // with platform index
        val rbc = Channels.newChannel(url.openStream())
        fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
        rbc.close()
        println(successMsg)
        exitProcess(0)
    }
    catch (e:Exception){
        try {
            val url = URL("$urlGitHub/${getLastVer()}/$newApp")
            val rbc = Channels.newChannel(url.openStream())
            fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
            rbc.close()
            println(successMsg)
            exitProcess(0)
        }
        catch (e:Exception){
            fos.close()
            println("Failed to download!")
        }
    }
}